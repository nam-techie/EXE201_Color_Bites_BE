package com.exe201.color_bites_be.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO nhận webhook callback từ PayOS
 * Theo PayOS webhook specification
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PayOSWebhookRequest {
    
    /**
     * Mã phản hồi từ PayOS ("00" = success)
     */
    @JsonProperty("code")
    private String code;
    
    /**
     * Mô tả phản hồi
     */
    @JsonProperty("desc")
    private String desc;
    
    /**
     * Dữ liệu giao dịch
     */
    @JsonProperty("data")
    private WebhookData data;
    
    /**
     * Chữ ký từ PayOS để verify
     */
    @JsonProperty("signature")
    private String signature;
    
    /**
     * Inner class chứa data của webhook
     */
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class WebhookData {
        
        @JsonProperty("orderCode")
        private Long orderCode;
        
        @JsonProperty("amount")
        private Long amount;
        
        @JsonProperty("description")
        private String description;
        
        @JsonProperty("accountNumber")
        private String accountNumber;
        
        @JsonProperty("reference")
        private String reference;
        
        @JsonProperty("transactionDateTime")
        private String transactionDateTime;
        
        @JsonProperty("paymentLinkId")
        private String paymentLinkId;
        
        @JsonProperty("code")
        private String code;
        
        @JsonProperty("desc")
        private String desc;
        
        @JsonProperty("counterAccountBankId")
        private String counterAccountBankId;
        
        @JsonProperty("counterAccountBankName")
        private String counterAccountBankName;
        
        @JsonProperty("counterAccountName")
        private String counterAccountName;
        
        @JsonProperty("counterAccountNumber")
        private String counterAccountNumber;
        
        @JsonProperty("virtualAccountName")
        private String virtualAccountName;
        
        @JsonProperty("virtualAccountNumber")
        private String virtualAccountNumber;
    }
}

