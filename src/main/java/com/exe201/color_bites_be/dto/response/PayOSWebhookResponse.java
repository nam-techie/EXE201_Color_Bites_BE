package com.exe201.color_bites_be.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response trả về cho PayOS webhook
 * Theo PayOS specification: {"code": "00", "desc": "success"}
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PayOSWebhookResponse {
    
    /**
     * Mã phản hồi ("00" = success, "01" = error)
     */
    @JsonProperty("code")
    private String code;
    
    /**
     * Mô tả phản hồi
     */
    @JsonProperty("desc")
    private String desc;
    
    /**
     * Helper method tạo success response
     */
    public static PayOSWebhookResponse success() {
        return new PayOSWebhookResponse("00", "success");
    }
    
    /**
     * Helper method tạo error response
     */
    public static PayOSWebhookResponse error(String message) {
        return new PayOSWebhookResponse("01", message);
    }
}

