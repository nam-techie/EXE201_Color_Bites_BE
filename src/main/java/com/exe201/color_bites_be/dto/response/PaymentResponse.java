package com.exe201.color_bites_be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response DTO cho thanh toán
 * Trả về thông tin cần thiết cho mobile app
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentResponse {
    
    /**
     * URL thanh toán để mobile app redirect
     */
    private String checkoutUrl;
    
    /**
     * ID giao dịch từ PayOS
     */
    private String paymentLinkId;
    
    /**
     * Order code duy nhất
     */
    private Long orderCode;
    
    /**
     * QR Code URL (nếu có)
     */
    private String qrCode;
    
    /**
     * Trạng thái tạo thanh toán
     */
    private String status;
    
    /**
     * Thời gian tạo
     */
    private String createdAt;
    
    /**
     * Thông báo cho user
     */
    private String message;
}
