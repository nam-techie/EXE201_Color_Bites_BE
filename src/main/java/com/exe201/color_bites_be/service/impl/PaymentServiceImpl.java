package com.exe201.color_bites_be.service.impl;

import com.exe201.color_bites_be.config.PayOSConfig;
import com.exe201.color_bites_be.dto.request.CreatePaymentRequest;
import com.exe201.color_bites_be.dto.response.PaymentResponse;
import com.exe201.color_bites_be.dto.response.PaymentStatusResponse;
import com.exe201.color_bites_be.entity.Transaction;
import com.exe201.color_bites_be.enums.CurrencyCode;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    public PaymentResponse createSubscriptionPayment(CreatePaymentRequest request, String accountId) {
        log.info("Creating subscription payment for account: {}", accountId);
        
        try {
            // Tạo orderCode unique
            String orderCode = generateUniqueOrderCode();
            
            // Tạo Transaction record trước
            Transaction transaction = createTransactionRecord(request, accountId, orderCode);
            transaction = transactionRepository.save(transaction);
            
            // Gọi PayOS để tạo thanh toán
            PayOSPaymentResponse payosResponse = createPayOSPayment(request, orderCode, accountId);
            
            if (payosResponse.isSuccess()) {
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
                // Cập nhật transaction thành FAILED
                transaction.setStatus(TxnStatus.FAILED);
                transaction.setUpdatedAt(LocalDateTime.now());
                transactionRepository.save(transaction);
                
                throw new RuntimeException("Không thể tạo thanh toán từ PayOS: " + payosResponse.getErrorMessage());
            }
            
        } catch (Exception e) {
            log.error("Error creating subscription payment: ", e);
            throw new RuntimeException("Lỗi tạo thanh toán: " + e.getMessage());
        }
    }
    
    @Override
    public PaymentStatusResponse checkPaymentStatus(String transactionId, String accountId) {
        log.info("Checking payment status for transaction: {} by account: {}", transactionId, accountId);
        
        try {
            // Tìm transaction trong DB
            Optional<Transaction> transactionOpt = transactionRepository.findByProviderTxnId(transactionId);
            
            if (transactionOpt.isEmpty()) {
                throw new RuntimeException("Không tìm thấy giao dịch");
            }
            
            Transaction transaction = transactionOpt.get();
            
            // Kiểm tra quyền truy cập
            if (!transaction.getAccountId().equals(accountId)) {
                throw new RuntimeException("Không có quyền truy cập giao dịch này");
            }
            
            // Kiểm tra status từ PayOS
            PayOSStatusResponse gatewayStatus = checkPayOSPaymentStatus(transactionId);
            
            // Cập nhật status nếu khác
            TxnStatus currentStatus = mapPayOSStatusToTxnStatus(gatewayStatus.getStatus());
            if (!currentStatus.equals(transaction.getStatus())) {
                transaction.setStatus(currentStatus);
                transaction.setUpdatedAt(LocalDateTime.now());
                transactionRepository.save(transaction);
                
                // Xử lý logic theo status
                if (currentStatus == TxnStatus.SUCCESS) {
                    processSuccessfulPayment(transaction);
                } else if (currentStatus == TxnStatus.FAILED) {
                    processFailedPayment(transaction);
                }
            }
            
            return PaymentStatusResponse.builder()
                .transactionId(transactionId)
                .orderCode(Long.valueOf(transaction.getOrderCode()))
                .status(currentStatus)
                .amount(transaction.getAmount().longValue())
                .description(getMetadataValue(transaction, "description"))
                .gatewayName(transaction.getGateway())
                .message(getStatusMessage(currentStatus))
                .createdAt(transaction.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .updatedAt(transaction.getUpdatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .build();
                
        } catch (Exception e) {
            log.error("Error checking payment status: ", e);
            throw new RuntimeException("Lỗi kiểm tra trạng thái thanh toán: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public boolean handlePaymentCallback(Map<String, String> callbackData) {
        log.info("Handling payment callback: {}", callbackData);
        
        try {
            // Verify signature
            if (!verifyPayOSSignature(callbackData)) {
                log.error("Invalid signature từ PayOS");
                return false;
            }
            
            String orderCode = callbackData.get("orderCode");
            String status = callbackData.get("status");
            
            // Tìm transaction theo orderCode
            Optional<Transaction> transactionOpt = transactionRepository.findByOrderCode(orderCode);
            
            if (transactionOpt.isPresent()) {
                Transaction transaction = transactionOpt.get();
                
                // Cập nhật status
                TxnStatus newStatus = mapPayOSStatusToTxnStatus(status);
                transaction.setStatus(newStatus);
                transaction.setUpdatedAt(LocalDateTime.now());
                
                // Lưu raw payload
                transaction.setRawPayload(new HashMap<>(callbackData));
                
                transactionRepository.save(transaction);
                
                // Xử lý logic theo status
                if (newStatus == TxnStatus.SUCCESS) {
                    processSuccessfulPayment(transaction);
                } else if (newStatus == TxnStatus.FAILED) {
                    processFailedPayment(transaction);
                }
                
                return true;
            }
            
            return false;
        } catch (Exception e) {
            log.error("Error handling payment callback: ", e);
            return false;
        }
    }
    
    @Override
    public List<Transaction> getTransactionHistory(String accountId) {
        return transactionRepository.findByAccountIdOrderByCreatedAtDesc(accountId);
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
            requestBody.put("amount", request.getAmount());
            requestBody.put("description", request.getDescription());
            requestBody.put("returnUrl", request.getReturnUrl() != null ? request.getReturnUrl() : payOSConfig.getReturnUrl());
            requestBody.put("cancelUrl", request.getCancelUrl() != null ? request.getCancelUrl() : payOSConfig.getCancelUrl());
            
            // Tạo signature
            String signature = createPayOSSignature(requestBody);
            
            // Gọi PayOS API
            String jsonBody = objectMapper.writeValueAsString(requestBody);
            RequestBody body = RequestBody.create(jsonBody, MediaType.get("application/json"));
            
            Request httpRequest = new Request.Builder()
                    .url(payOSConfig.getApiUrl() + "/v2/payment-requests")
                    .post(body)
                    .addHeader("x-client-id", payOSConfig.getClientId())
                    .addHeader("x-api-key", payOSConfig.getApiKey())
                    .addHeader("Content-Type", "application/json")
                    .build();
            
            try (Response response = httpClient.newCall(httpRequest).execute()) {
                String responseBody = response.body().string();
                log.info("PayOS API response: {}", responseBody);
                
                if (response.isSuccessful()) {
                    JsonNode jsonResponse = objectMapper.readTree(responseBody);
                    JsonNode data = jsonResponse.get("data");
                    
                    return new PayOSPaymentResponse(
                        true,
                        data.get("checkoutUrl").asText(),
                        String.valueOf(orderCode),
                        data.has("qrCode") ? data.get("qrCode").asText() : null,
                        null
                    );
                } else {
                    log.error("PayOS API error: {}", responseBody);
                    return new PayOSPaymentResponse(false, null, null, null, responseBody);
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
     * Tạo signature cho PayOS request
     */
    private String createPayOSSignature(Map<String, Object> data) {
        // PayOS signature format: amount&cancelUrl&description&orderCode&returnUrl
        String signatureData = String.format("%s&%s&%s&%s&%s",
            data.get("amount"),
            data.get("cancelUrl"),
            data.get("description"),
            data.get("orderCode"),
            data.get("returnUrl")
        );
        
        return HmacUtils.hmacSha256Hex(payOSConfig.getChecksumKey(), signatureData);
    }
    
    /**
     * Verify signature từ PayOS callback
     */
    private boolean verifyPayOSSignature(Map<String, String> params) {
        try {
            String receivedSignature = params.get("signature");
            if (receivedSignature == null) return false;
            
            // Tạo lại signature để so sánh
            String dataToSign = String.format("%s&%s&%s&%s&%s",
                params.get("amount"),
                params.get("cancelUrl"),
                params.get("description"),
                params.get("orderCode"),
                params.get("returnUrl")
            );
            
            String calculatedSignature = HmacUtils.hmacSha256Hex(payOSConfig.getChecksumKey(), dataToSign);
            
            return calculatedSignature.equals(receivedSignature);
        } catch (Exception e) {
            log.error("Lỗi verify signature: ", e);
            return false;
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
        metadata.put("orderInfo", request.getOrderInfo());
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