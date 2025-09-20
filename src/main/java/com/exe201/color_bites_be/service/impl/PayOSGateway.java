package com.exe201.color_bites_be.service.impl;

import com.exe201.color_bites_be.config.PayOSConfig;
import com.exe201.color_bites_be.entity.Transaction;
import com.exe201.color_bites_be.enums.CurrencyCode;
import com.exe201.color_bites_be.enums.TxnStatus;
import com.exe201.color_bites_be.enums.TxnType;
import com.exe201.color_bites_be.repository.AccountRepository;
import com.exe201.color_bites_be.service.IPaymentGateway;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation của PayOS Payment Gateway
 * Xử lý tất cả tương tác với PayOS API
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PayOSGateway implements IPaymentGateway {
    
    private final PayOSConfig payOSConfig;
    private final MongoTemplate mongoTemplate;
    private final AccountRepository accountRepository;
    private final ObjectMapper objectMapper;
    private final OkHttpClient httpClient = new OkHttpClient();
    
    @Override
    public PaymentInitResponse createPayment(OrderInfo orderInfo) {
        try {
            log.info("Tạo thanh toán PayOS cho order: {}", orderInfo.getOrderId());
            
            // Tạo order code unique (timestamp + random)
            long orderCode = System.currentTimeMillis();
            
            // Tạo request body cho PayOS
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("orderCode", orderCode);
            requestBody.put("amount", orderInfo.getAmount());
            requestBody.put("description", orderInfo.getDescription());
            requestBody.put("returnUrl", payOSConfig.getReturnUrl());
            requestBody.put("cancelUrl", payOSConfig.getCancelUrl());
            
            // Tạo signature
            String signature = createSignature(requestBody);
            
            // Gọi PayOS API
            String jsonBody = objectMapper.writeValueAsString(requestBody);
            RequestBody body = RequestBody.create(jsonBody, MediaType.get("application/json"));
            
            Request request = new Request.Builder()
                    .url(payOSConfig.getApiUrl() + "/v2/payment-requests")
                    .post(body)
                    .addHeader("x-client-id", payOSConfig.getClientId())
                    .addHeader("x-api-key", payOSConfig.getApiKey())
                    .addHeader("Content-Type", "application/json")
                    .build();
            
            try (Response response = httpClient.newCall(request).execute()) {
                String responseBody = response.body().string();
                log.info("PayOS API response: {}", responseBody);
                
                if (response.isSuccessful()) {
                    JsonNode jsonResponse = objectMapper.readTree(responseBody);
                    JsonNode data = jsonResponse.get("data");
                    
                    // Lưu transaction vào database
                    Transaction transaction = saveTransaction(orderInfo, orderCode, TxnStatus.PENDING);
                    
                    return new PaymentInitResponseImpl(
                        data.get("checkoutUrl").asText(),
                        String.valueOf(orderCode),
                        "SUCCESS"
                    );
                } else {
                    log.error("PayOS API error: {}", responseBody);
                    return new PaymentInitResponseImpl(null, null, "FAILED");
                }
            }
            
        } catch (Exception e) {
            log.error("Lỗi tạo thanh toán PayOS: ", e);
            return new PaymentInitResponseImpl(null, null, "ERROR");
        }
    }
    
    @Override
    public PaymentCallbackResult handleCallback(Map<String, String> params) {
        try {
            log.info("Xử lý callback PayOS: {}", params);
            
            // Verify signature
            if (!verifySignature(params)) {
                log.error("Invalid signature từ PayOS");
                return new PaymentCallbackResultImpl(false, null, "Invalid signature");
            }
            
            String orderCode = params.get("orderCode");
            String status = params.get("status");
            
            // Cập nhật transaction status
            TxnStatus txnStatus = mapPayOSStatusToTxnStatus(status);
            updateTransactionStatus(orderCode, txnStatus);
            
            return new PaymentCallbackResultImpl(
                "SUCCESS".equals(status),
                orderCode,
                "Callback processed successfully"
            );
            
        } catch (Exception e) {
            log.error("Lỗi xử lý callback PayOS: ", e);
            return new PaymentCallbackResultImpl(false, null, "Callback processing failed");
        }
    }
    
    @Override
    public PaymentStatus checkPaymentStatus(String transactionId) {
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
                    
                    return mapPayOSStatusToPaymentStatus(status);
                } else {
                    log.error("PayOS API error khi check status: {}", responseBody);
                    return PaymentStatus.FAILED;
                }
            }
            
        } catch (Exception e) {
            log.error("Lỗi kiểm tra trạng thái PayOS: ", e);
            return PaymentStatus.FAILED;
        }
    }
    
    @Override
    public String getGatewayName() {
        return "PayOS";
    }
    
    /**
     * Tạo signature cho PayOS request
     */
    private String createSignature(Map<String, Object> data) {
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
    private boolean verifySignature(Map<String, String> params) {
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
    
    /**
     * Lưu transaction vào database
     */
    private Transaction saveTransaction(OrderInfo orderInfo, long orderCode, TxnStatus status) {
        Transaction transaction = new Transaction();
        transaction.setAccountId(extractAccountIdFromCustomerInfo(orderInfo.getCustomerInfo()));
        transaction.setAmount(Double.valueOf(orderInfo.getAmount()));
        transaction.setCurrency(CurrencyCode.valueOf(orderInfo.getCurrency()));
        transaction.setType(TxnType.PAYMENT);
        transaction.setStatus(status);
        transaction.setCreatedAt(LocalDateTime.now());
        
        // Metadata
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("orderCode", orderCode);
        metadata.put("gateway", "PayOS");
        metadata.put("description", orderInfo.getDescription());
        transaction.setMetadata(metadata);
        
        return mongoTemplate.save(transaction);
    }
    
    /**
     * Cập nhật trạng thái transaction
     */
    private void updateTransactionStatus(String orderCode, TxnStatus status) {
        // Tìm và cập nhật transaction theo orderCode
        // Implementation tùy thuộc vào cách query MongoDB
    }
    
    /**
     * Map PayOS status sang TxnStatus
     */
    private TxnStatus mapPayOSStatusToTxnStatus(String payosStatus) {
        return switch (payosStatus.toUpperCase()) {
            case "PAID" -> TxnStatus.SUCCESS;
            case "CANCELLED" -> TxnStatus.CANCELED;
            case "PENDING" -> TxnStatus.PENDING;
            default -> TxnStatus.FAILED;
        };
    }
    
    /**
     * Map PayOS status sang PaymentStatus
     */
    private PaymentStatus mapPayOSStatusToPaymentStatus(String payosStatus) {
        return switch (payosStatus.toUpperCase()) {
            case "PAID" -> PaymentStatus.SUCCESS;
            case "CANCELLED" -> PaymentStatus.CANCELLED;
            case "PENDING" -> PaymentStatus.PENDING;
            default -> PaymentStatus.FAILED;
        };
    }
    
    /**
     * Extract account ID từ customer info
     */
    private String extractAccountIdFromCustomerInfo(String customerInfo) {
        // Giả sử customerInfo format: "accountId:xxx"
        if (customerInfo != null && customerInfo.startsWith("accountId:")) {
            return customerInfo.substring("accountId:".length());
        }
        return null;
    }
    
    // Implementation classes cho interfaces
    private record PaymentInitResponseImpl(String paymentUrl, String transactionId, String status) 
            implements PaymentInitResponse {
        @Override
        public String getPaymentUrl() { return paymentUrl; }
        @Override
        public String getTransactionId() { return transactionId; }
        @Override
        public String getStatus() { return status; }
    }
    
    private record PaymentCallbackResultImpl(boolean success, String transactionId, String message)
            implements PaymentCallbackResult {
        @Override
        public boolean isSuccess() { return success; }
        @Override
        public String getTransactionId() { return transactionId; }
        @Override
        public String getMessage() { return message; }
    }
}
