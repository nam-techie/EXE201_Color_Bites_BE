package com.exe201.color_bites_be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO cho PayOS Webhook
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PayOSWebhookResponse {
    
    private String code;
    private String desc;
}
