package com.exe201.color_bites_be.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Request DTO cho PayOS Webhook
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PayOSWebhookRequest {
    
    @JsonProperty("code")
    private String code;
    
    @JsonProperty("desc")
    private String desc;
    
    @JsonProperty("data")
    private PayOSWebhookData data;
    
    @JsonProperty("signature")
    private String signature;
    
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PayOSWebhookData {
        @JsonProperty("orderCode")
        private Long orderCode;
        
        @JsonProperty("paymentLinkId")
        private String paymentLinkId;
        
        @JsonProperty("amount")
        private Integer amount;
        
        @JsonProperty("description")
        private String description;
        
        @JsonProperty("status")
        private String status;
        
        @JsonProperty("qrCode")
        private String qrCode;
        
        @JsonProperty("checkoutUrl")
        private String checkoutUrl;
        
        // Additional fields that PayOS might send
        private Map<String, Object> additionalData;
    }
}
