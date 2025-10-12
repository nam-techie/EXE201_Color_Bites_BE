package com.exe201.color_bites_be.service.impl;

import com.exe201.color_bites_be.config.PayOSConfig;
import com.exe201.color_bites_be.dto.request.CreatePaymentRequest;
import com.exe201.color_bites_be.dto.response.PaymentResponse;
import com.exe201.color_bites_be.dto.response.PaymentStatusResponse;
import com.exe201.color_bites_be.entity.Account;
import com.exe201.color_bites_be.entity.Transaction;
import com.exe201.color_bites_be.enums.SubcriptionPlan;
import com.exe201.color_bites_be.enums.TransactionEnums.TxnStatus;
import com.exe201.color_bites_be.enums.TransactionEnums.TxnType;
import com.exe201.color_bites_be.repository.TransactionRepository;
import com.exe201.color_bites_be.service.IPaymentService;
import com.exe201.color_bites_be.service.ISubscriptionService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementation của IPaymentService
 * Gộp toàn bộ logic PayOS và Payment processing trong một file
 * Tối ưu từ 7 files → 4 files
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements IPaymentService {
    
    private final TransactionRepository transactionRepository;
    private final ISubscriptionService subscriptionService;
    private final PayOSConfig payOSConfig;
    private final ObjectMapper objectMapper;
    private final OkHttpClient httpClient = new OkHttpClient();
    
    // ==================== PUBLIC API METHODS ====================
    
    @Override
    @Transactional
    public PaymentResponse createSubscriptionPayment(CreatePaymentRequest request) {
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        try {
            log.info("Bắt đầu tạo thanh toán subscription cho user: {}", account.getId());
            
            // Tạo orderCode unique
            String orderCode = generateUniqueOrderCode();
            log.info("Generated orderCode: {}", orderCode);
            
            // Tạo Transaction record trước
            Transaction transaction = createTransactionRecord(request, account.getId(), orderCode);
            transaction = transactionRepository.save(transaction);
            log.info("Created transaction record: {}", transaction.getId());
            
            // Gọi PayOS để tạo thanh toán
            PayOSPaymentResponse payosResponse = createPayOSPayment(request, orderCode, account.getId());
            
            if (payosResponse.isSuccess()) {
                log.info("PayOS payment created successfully for order: {}", orderCode);
                
                // Cập nhật providerTxnId
                transaction.setProviderTxnId(payosResponse.getTransactionId());
                transaction.setUpdatedAt(LocalDateTime.now());
                transactionRepository.save(transaction);
                
                return PaymentResponse.builder()
                    .checkoutUrl(payosResponse.getPaymentUrl())
                    .paymentLinkId(payosResponse.getTransactionId())
                    .orderCode(Long.valueOf(orderCode))
                    .qrCode(payosResponse.getQrCode())
                    .status("SUCCESS")
                    .createdAt(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    .message("Tạo thanh toán thành công")
                    .build();
            } else {
                log.error("PayOS payment creation failed: {}", payosResponse.getErrorMessage());
                
                // Cập nhật transaction thành FAILED
                transaction.setStatus(TxnStatus.FAILED);
                transaction.setUpdatedAt(LocalDateTime.now());
                transactionRepository.save(transaction);
                
                // Trả về lỗi chi tiết thay vì throw exception
                throw new RuntimeException("Không thể tạo thanh toán từ PayOS: " + payosResponse.getErrorMessage());
            }
            
        } catch (RuntimeException e) {
            // Re-throw RuntimeException để giữ nguyên error message
            log.error("Runtime error creating subscription payment: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error creating subscription payment: ", e);
            throw new RuntimeException("Lỗi không mong muốn khi tạo thanh toán: " + e.getMessage());
        }
    }
    
    
    
    
    @Override
    @Transactional
    public void processSuccessfulPayment(Transaction transaction) {
        log.info("Processing successful payment for transaction: {}", transaction.getId());
        
        try {
            // Nâng cấp subscription lên PREMIUM
            subscriptionService.upgradeToPremium(transaction.getAccountId());
            
            log.info("Successfully upgraded account {} to PREMIUM after payment", 
                transaction.getAccountId());
        } catch (Exception e) {
            log.error("Error processing successful payment: ", e);
            // Có thể cần rollback hoặc retry logic ở đây
        }
    }
    
    @Override
    public void processFailedPayment(Transaction transaction) {
        log.info("Processing failed payment for transaction: {}", transaction.getId());
        
        // Có thể gửi notification hoặc log cho admin
        // Không cần làm gì đặc biệt vì user vẫn ở FREE plan
    }
    
    // ==================== PAYOS INTEGRATION METHODS ====================
    
    /**
     * Tạo thanh toán với PayOS API
     */
    private PayOSPaymentResponse createPayOSPayment(CreatePaymentRequest request, String orderCode, String accountId) {
        try {
            log.info("Tạo thanh toán PayOS cho order: {}", orderCode);
            
            // Tạo request body cho PayOS
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("orderCode", Long.valueOf(orderCode));
            // Đảm bảo amount là số nguyên (VND không có decimal)
            requestBody.put("amount", request.getAmount().intValue());
            requestBody.put("description", request.getDescription());
            requestBody.put("returnUrl", request.getReturnUrl() != null ? request.getReturnUrl() : payOSConfig.getReturnUrl());
            requestBody.put("cancelUrl", request.getCancelUrl() != null ? request.getCancelUrl() : payOSConfig.getCancelUrl());
            
            // Thêm items array
            List<Map<String, Object>> items = new ArrayList<>();
            for (CreatePaymentRequest.PaymentItem item : request.getItems()) {
                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("name", item.getName());
                itemMap.put("quantity", item.getQuantity());
                itemMap.put("price", item.getPrice());
                items.add(itemMap);
            }
            requestBody.put("items", items);
            
            // Tạo signature
            String signature = createPayOSSignature(requestBody);
            requestBody.put("signature", signature);  // Thêm signature vào request body
            
            // Gọi PayOS API
            String jsonBody = objectMapper.writeValueAsString(requestBody);
            log.info("PayOS request body: {}", jsonBody);  // Debug request
            RequestBody body = RequestBody.create(jsonBody, MediaType.get("application/json"));
            
            Request httpRequest = new Request.Builder()
                    .url(payOSConfig.getApiUrl() + "/v2/payment-requests")
                    .post(body)
                    .addHeader("x-client-id", payOSConfig.getClientId())
                    .addHeader("x-api-key", payOSConfig.getApiKey())
                    .addHeader("Content-Type", "application/json")
                    .build();
            
            log.info("PayOS request URL: {}", httpRequest.url());
            log.info("PayOS request headers: {}", httpRequest.headers());
            
            try (Response response = httpClient.newCall(httpRequest).execute()) {
                String responseBody = response.body().string();
                log.info("PayOS API response status: {}, body: {}", response.code(), responseBody);
                
                if (response.isSuccessful()) {
                    JsonNode jsonResponse = objectMapper.readTree(responseBody);
                    log.info("PayOS response structure: {}", jsonResponse.toPrettyString());  // Debug response structure
                    
                    // Kiểm tra response structure chi tiết hơn
                    if (!jsonResponse.has("data")) {
                        log.error("PayOS response missing 'data' field. Full response: {}", responseBody);
                        return new PayOSPaymentResponse(false, null, null, null, "PayOS response missing 'data' field. Full response: " + responseBody);
                    }
                    
                    JsonNode data = jsonResponse.get("data");
                    
                    // Kiểm tra nếu data là null hoặc empty object
                    if (data == null || data.isEmpty()) {
                        log.error("PayOS response data is null or empty. Response: {}", responseBody);
                        return new PayOSPaymentResponse(false, null, null, null, "PayOS response data is empty. Full response: " + responseBody);
                    }
                    
                    // Log tất cả fields có trong data để debug
                    log.info("PayOS data fields: {}", data.fieldNames().toString());
                    
                    // Kiểm tra các field có thể có trong response
                    String checkoutUrl = null;
                    String paymentLinkId = null;
                    
                    if (data.has("checkoutUrl")) {
                        checkoutUrl = data.get("checkoutUrl").asText();
                        log.info("Found checkoutUrl: {}", checkoutUrl);
                    } else if (data.has("paymentUrl")) {
                        checkoutUrl = data.get("paymentUrl").asText();
                        log.info("Found paymentUrl: {}", checkoutUrl);
                    } else if (data.has("url")) {
                        checkoutUrl = data.get("url").asText();
                        log.info("Found url: {}", checkoutUrl);
                    } else {
                        // Log tất cả available fields để debug
                        List<String> availableFields = new ArrayList<>();
                        data.fieldNames().forEachRemaining(availableFields::add);
                        log.error("PayOS response missing checkoutUrl/paymentUrl/url. Available fields: {}", availableFields);
                        return new PayOSPaymentResponse(false, null, null, null, 
                            "Missing checkoutUrl in response. Available fields: " + availableFields.toString());
                    }
                    
                    // Lấy paymentLinkId từ response
                    if (data.has("paymentLinkId")) {
                        paymentLinkId = data.get("paymentLinkId").asText();
                        log.info("Found paymentLinkId: {}", paymentLinkId);
                    }
                    
                    if (checkoutUrl == null || checkoutUrl.isEmpty()) {
                        log.error("PayOS checkoutUrl is null or empty");
                        return new PayOSPaymentResponse(false, null, null, null, "PayOS checkoutUrl is empty");
                    }
                    
                    // Dùng paymentLinkId nếu có, fallback về orderCode
                    String transactionId = paymentLinkId != null ? paymentLinkId : String.valueOf(orderCode);
                    
                    return new PayOSPaymentResponse(
                        true,
                        checkoutUrl,
                        transactionId,
                        data.has("qrCode") ? data.get("qrCode").asText() : null,
                        null
                    );
                } else {
                    log.error("PayOS API error - Status: {}, Body: {}", response.code(), responseBody);
                    return new PayOSPaymentResponse(false, null, null, null, "PayOS API error: " + responseBody);
                }
            }
            
        } catch (Exception e) {
            log.error("Lỗi tạo thanh toán PayOS: ", e);
            return new PayOSPaymentResponse(false, null, null, null, e.getMessage());
        }
    }
    
    /**
     * Kiểm tra trạng thái thanh toán từ PayOS
     */
    private PayOSStatusResponse checkPayOSPaymentStatus(String transactionId) {
        try {
            log.info("Kiểm tra trạng thái thanh toán: {}", transactionId);
            
            Request request = new Request.Builder()
                    .url(payOSConfig.getApiUrl() + "/v2/payment-requests/" + transactionId)
                    .get()
                    .addHeader("x-client-id", payOSConfig.getClientId())
                    .addHeader("x-api-key", payOSConfig.getApiKey())
                    .build();
            
            try (Response response = httpClient.newCall(request).execute()) {
                String responseBody = response.body().string();
                
                if (response.isSuccessful()) {
                    JsonNode jsonResponse = objectMapper.readTree(responseBody);
                    JsonNode data = jsonResponse.get("data");
                    String status = data.get("status").asText();
                    
                    return new PayOSStatusResponse(true, status, null);
                } else {
                    log.error("PayOS API error khi check status: {}", responseBody);
                    return new PayOSStatusResponse(false, "FAILED", responseBody);
                }
            }
            
        } catch (Exception e) {
            log.error("Lỗi kiểm tra trạng thái PayOS: ", e);
            return new PayOSStatusResponse(false, "FAILED", e.getMessage());
        }
    }
    
    /**
     * Tạo signature cho PayOS request theo đúng specification
     * PayOS yêu cầu: key=value format, sort alphabet, chỉ 5 field bắt buộc
     */
    private String createPayOSSignature(Map<String, Object> body) {
        // PayOS chỉ yêu cầu 5 field này cho signature (KHÔNG include items)
        Map<String, Object> data = new java.util.TreeMap<>();
        Object amount = body.get("amount");
        Object cancelUrl = body.get("cancelUrl");
        Object description = body.get("description");
        Object orderCode = body.get("orderCode");
        Object returnUrl = body.get("returnUrl");

        data.put("amount", amount == null ? "" : amount);
        data.put("cancelUrl", cancelUrl == null ? "" : cancelUrl);
        data.put("description", description == null ? "" : description);
        data.put("orderCode", orderCode == null ? "" : orderCode);
        data.put("returnUrl", returnUrl == null ? "" : returnUrl);

        String dataStr = data.entrySet().stream()
            .map(e -> e.getKey() + "=" + String.valueOf(e.getValue()))
            .collect(java.util.stream.Collectors.joining("&"));

        log.info("PayOS signature data (correct format): {}", dataStr);
        log.info("PayOS checksum key length: {}", payOSConfig.getChecksumKey() != null ? payOSConfig.getChecksumKey().length() : "NULL");
        
        String signature = HmacUtils.hmacSha256Hex(payOSConfig.getChecksumKey(), dataStr);
        log.info("PayOS signature: {}", signature);
        return signature;
    }
    
    
    
    // ==================== CONFIRM PAYMENT METHOD ====================
    
    /**
     * Confirm payment status từ FE (an toàn - gọi PayOS để verify)
     */
    public PaymentStatusResponse updateStatusFromGateway(String id) {
        try {
            log.info("Confirming payment status for ID: {}", id);
            
            // 1) Tìm transaction theo providerTxnId (paymentLinkId) trước, không có thì fallback orderCode
            Optional<Transaction> opt = transactionRepository.findByProviderTxnId(id);
            if (opt.isEmpty()) {
                opt = transactionRepository.findByOrderCode(id);
            }
            if (opt.isEmpty()) {
                throw new RuntimeException("Không tìm thấy giao dịch với ID: " + id);
            }
            
            Transaction tx = opt.get();
            log.info("Found transaction: {} with current status: {}", tx.getId(), tx.getStatus());
            
            // 2) Hỏi PayOS (server-to-server)
            PayOSStatusResponse gw = checkPayOSPaymentStatus(id);
            TxnStatus newStatus = mapPayOSStatusToTxnStatus(gw.getStatus());
            log.info("PayOS status: {} -> Mapped status: {}", gw.getStatus(), newStatus);
            
            // 3) Cập nhật DB (idempotent)
            if (newStatus != tx.getStatus()) {
                log.info("Updating transaction status from {} to {}", tx.getStatus(), newStatus);
                tx.setStatus(newStatus);
                tx.setUpdatedAt(LocalDateTime.now());
                transactionRepository.save(tx);
                
                // Process business logic
                if (newStatus == TxnStatus.SUCCESS) {
                    processSuccessfulPayment(tx);
                } else if (newStatus == TxnStatus.FAILED) {
                    processFailedPayment(tx);
                }
            } else {
                log.info("Transaction status unchanged: {}", newStatus);
            }
            
            // 4) Trả về cho FE trạng thái cuối cùng
            return PaymentStatusResponse.builder()
                .transactionId(tx.getProviderTxnId() != null ? tx.getProviderTxnId() : tx.getOrderCode())
                .orderCode(Long.valueOf(tx.getOrderCode()))
                .status(tx.getStatus())
                .amount(tx.getAmount().longValue())
                .description(getMetadataValue(tx, "description"))
                .gatewayName(tx.getGateway())
                .message(getStatusMessage(tx.getStatus()))
                .createdAt(tx.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .updatedAt(tx.getUpdatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .build();
                
        } catch (Exception e) {
            log.error("Error confirming payment status: ", e);
            throw new RuntimeException("Lỗi xác nhận thanh toán: " + e.getMessage());
        }
    }
    
    
    // ==================== HELPER METHODS ====================
    
    
    
    
    
    /**
     * Tạo Transaction record
     */
    private Transaction createTransactionRecord(CreatePaymentRequest request, String accountId, String orderCode) {
        Transaction transaction = new Transaction();
        transaction.setAccountId(accountId);
        transaction.setAmount(request.getAmount().doubleValue());
        transaction.setCurrency(request.getCurrency());
        transaction.setType(TxnType.SUBSCRIPTION);
        transaction.setStatus(TxnStatus.PENDING);
        transaction.setOrderCode(orderCode);
        transaction.setPlan(SubcriptionPlan.PREMIUM);
        transaction.setGateway("PayOS");
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setUpdatedAt(LocalDateTime.now());
        
        // Metadata
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("description", request.getDescription());
        metadata.put("items", request.getItems());
        transaction.setMetadata(metadata);
        
        return transaction;
    }
    
    /**
     * Generate unique order code
     */
    private String generateUniqueOrderCode() {
        String orderCode;
        do {
            orderCode = String.valueOf(System.currentTimeMillis());
        } while (transactionRepository.existsByOrderCode(orderCode));
        
        return orderCode;
    }
    
    /**
     * Map PayOS status sang TxnStatus
     */
    private TxnStatus mapPayOSStatusToTxnStatus(String payosStatus) {
        return switch (payosStatus.toUpperCase()) {
            case "PAID", "SUCCESS" -> TxnStatus.SUCCESS;
            case "CANCELLED", "CANCELED" -> TxnStatus.CANCELED;
            case "PENDING" -> TxnStatus.PENDING;
            default -> TxnStatus.FAILED;
        };
    }
    
    /**
     * Lấy message theo status
     */
    private String getStatusMessage(TxnStatus status) {
        return switch (status) {
            case SUCCESS -> "Thanh toán thành công";
            case PENDING -> "Đang chờ thanh toán";
            case CANCELED -> "Thanh toán đã bị hủy";
            case FAILED -> "Thanh toán thất bại";
            case REFUNDED -> "Đã hoàn tiền";
        };
    }
    
    /**
     * Lấy giá trị từ metadata
     */
    private String getMetadataValue(Transaction transaction, String key) {
        if (transaction.getMetadata() != null && transaction.getMetadata().containsKey(key)) {
            return transaction.getMetadata().get(key).toString();
        }
        return "";
    }
    
    // ==================== WEBHOOK HANDLING METHODS ====================
    
    /**
     * Xử lý webhook callback từ PayOS
     * Method này được gọi khi PayOS gửi notification về trạng thái thanh toán
     */
    @Override
    @Transactional
    public com.exe201.color_bites_be.dto.response.PayOSWebhookResponse handlePayOSWebhook(
            com.exe201.color_bites_be.dto.request.PayOSWebhookRequest request) {
        
        try {
            log.info("Nhận webhook từ PayOS - OrderCode: {}, Code: {}", 
                request.getData().getOrderCode(), request.getCode());
            
            // 1. Verify signature để đảm bảo request từ PayOS
            boolean isValidSignature = verifyWebhookSignature(request);
            if (!isValidSignature) {
                log.error("Webhook signature không hợp lệ!");
                return com.exe201.color_bites_be.dto.response.PayOSWebhookResponse.error("Invalid signature");
            }
            
            log.info("Webhook signature hợp lệ - Bắt đầu xử lý...");
            
            // 2. Kiểm tra webhook code
            if (!"00".equals(request.getCode())) {
                log.warn("Webhook code không phải success: {}", request.getCode());
                return com.exe201.color_bites_be.dto.response.PayOSWebhookResponse.error("Invalid webhook code");
            }
            
            // 3. Xử lý dữ liệu webhook
            processWebhookData(request.getData());
            
            log.info("Xử lý webhook thành công cho orderCode: {}", request.getData().getOrderCode());
            return com.exe201.color_bites_be.dto.response.PayOSWebhookResponse.success();
            
        } catch (Exception e) {
            log.error("Lỗi xử lý webhook: ", e);
            return com.exe201.color_bites_be.dto.response.PayOSWebhookResponse.error("Internal error: " + e.getMessage());
        }
    }
    
    /**
     * Verify signature của webhook từ PayOS
     * PayOS sử dụng HMAC-SHA256 để ký dữ liệu
     */
    private boolean verifyWebhookSignature(com.exe201.color_bites_be.dto.request.PayOSWebhookRequest request) {
        try {
            // PayOS webhook signature format: sort data fields alphabetically và concatenate
            // Theo PayOS spec: amount, code, desc, orderCode, ...
            com.exe201.color_bites_be.dto.request.PayOSWebhookRequest.WebhookData data = request.getData();
            
            // Tạo sorted data map (TreeMap tự động sort theo key)
            Map<String, Object> sortedData = new java.util.TreeMap<>();
            
            // Thêm các fields theo PayOS specification
            if (data.getAmount() != null) sortedData.put("amount", data.getAmount());
            if (data.getCode() != null) sortedData.put("code", data.getCode());
            if (data.getDesc() != null) sortedData.put("desc", data.getDesc());
            if (data.getOrderCode() != null) sortedData.put("orderCode", data.getOrderCode());
            if (data.getPaymentLinkId() != null) sortedData.put("paymentLinkId", data.getPaymentLinkId());
            
            // Tạo data string theo format: key=value&key=value
            String dataStr = sortedData.entrySet().stream()
                .map(e -> e.getKey() + "=" + String.valueOf(e.getValue()))
                .collect(java.util.stream.Collectors.joining("&"));
            
            log.info("Webhook signature data: {}", dataStr);
            
            // Tính signature
            String calculatedSignature = HmacUtils.hmacSha256Hex(payOSConfig.getChecksumKey(), dataStr);
            log.info("Calculated signature: {}", calculatedSignature);
            log.info("Received signature: {}", request.getSignature());
            
            // So sánh signature
            return calculatedSignature.equals(request.getSignature());
            
        } catch (Exception e) {
            log.error("Lỗi verify webhook signature: ", e);
            return false;
        }
    }
    
    /**
     * Xử lý dữ liệu từ webhook (idempotent)
     */
    private void processWebhookData(com.exe201.color_bites_be.dto.request.PayOSWebhookRequest.WebhookData data) {
        try {
            // 1. Tìm transaction theo orderCode
            String orderCode = String.valueOf(data.getOrderCode());
            Optional<Transaction> optTransaction = transactionRepository.findByOrderCode(orderCode);
            
            if (optTransaction.isEmpty()) {
                log.error("Không tìm thấy transaction với orderCode: {}", orderCode);
                throw new RuntimeException("Transaction not found: " + orderCode);
            }
            
            Transaction transaction = optTransaction.get();
            log.info("Tìm thấy transaction: {} - Current status: {}", 
                transaction.getId(), transaction.getStatus());
            
            // 2. Map webhook code sang TxnStatus
            TxnStatus newStatus = mapWebhookCodeToStatus(data.getCode());
            log.info("Webhook code {} mapped to status: {}", data.getCode(), newStatus);
            
            // 3. Update transaction (idempotent - chỉ update nếu status thay đổi)
            if (newStatus != transaction.getStatus()) {
                log.info("Cập nhật transaction status từ {} sang {}", 
                    transaction.getStatus(), newStatus);
                
                transaction.setStatus(newStatus);
                transaction.setUpdatedAt(LocalDateTime.now());
                
                // Update providerTxnId nếu có
                if (data.getPaymentLinkId() != null && transaction.getProviderTxnId() == null) {
                    transaction.setProviderTxnId(data.getPaymentLinkId());
                }
                
                // Lưu raw webhook payload để audit
                if (transaction.getRawPayload() == null) {
                    transaction.setRawPayload(new HashMap<>());
                }
                transaction.getRawPayload().put("webhook_data", data);
                transaction.getRawPayload().put("webhook_received_at", LocalDateTime.now().toString());
                
                transactionRepository.save(transaction);
                log.info("Đã lưu transaction với status mới: {}", newStatus);
                
                // 4. Process business logic
                if (newStatus == TxnStatus.SUCCESS) {
                    log.info("Thanh toán thành công - Bắt đầu nâng cấp subscription...");
                    processSuccessfulPayment(transaction);
                    log.info("Đã nâng cấp subscription thành công cho user: {}", transaction.getAccountId());
                } else if (newStatus == TxnStatus.FAILED) {
                    log.info("Thanh toán thất bại - Xử lý failed logic...");
                    processFailedPayment(transaction);
                }
            } else {
                log.info("Transaction status không thay đổi ({}), bỏ qua update", newStatus);
            }
            
        } catch (Exception e) {
            log.error("Lỗi xử lý webhook data: ", e);
            throw new RuntimeException("Error processing webhook data: " + e.getMessage());
        }
    }
    
    /**
     * Map webhook code từ PayOS sang TxnStatus
     */
    private TxnStatus mapWebhookCodeToStatus(String webhookCode) {
        // PayOS webhook codes:
        // "00" = Success
        // "01" = Failed
        // "02" = Pending
        return switch (webhookCode) {
            case "00" -> TxnStatus.SUCCESS;
            case "02" -> TxnStatus.PENDING;
            case "03" -> TxnStatus.CANCELED;
            default -> TxnStatus.FAILED;
        };
    }
    
    // ==================== INNER CLASSES ====================
    
    /**
     * Response từ PayOS API
     */
    private record PayOSPaymentResponse(
        boolean success,
        String paymentUrl,
        String transactionId,
        String qrCode,
        String errorMessage
    ) {
        public boolean isSuccess() { return success; }
        public String getPaymentUrl() { return paymentUrl; }
        public String getTransactionId() { return transactionId; }
        public String getQrCode() { return qrCode; }
        public String getErrorMessage() { return errorMessage; }
    }
    
    /**
     * Response status từ PayOS
     */
    private record PayOSStatusResponse(
        boolean success,
        String status,
        String errorMessage
    ) {
        public boolean isSuccess() { return success; }
        public String getStatus() { return status; }
        public String getErrorMessage() { return errorMessage; }
    }
}