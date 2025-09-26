package com.exe201.color_bites_be.dto;

import com.exe201.color_bites_be.enums.CurrencyCode;
import com.exe201.color_bites_be.enums.TransactionEnums.TxnStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Tất cả DTOs liên quan đến Payment được gộp trong file này
 * Giảm số lượng file từ 3 → 1
 */
public class PaymentDTOs {
    
    /**
     * Request DTO để tạo thanh toán mới
     * Chứa thông tin cần thiết cho mobile app gửi lên
     */
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreatePaymentRequest {
        
        /**
         * Mô tả đơn hàng (VD: "Thanh toán đơn hàng #123")
         */
        @NotBlank(message = "Mô tả đơn hàng không được để trống")
        private String description;
        
        /**
         * Số tiền thanh toán (VD: 100000 = 100,000 VND)
         */
        @NotNull(message = "Số tiền không được để trống")
        @DecimalMin(value = "1000", message = "Số tiền tối thiểu là 1,000 VND")
        private Long amount;
        
        /**
         * Mã tiền tệ (VND, USD)
         */
        @NotNull(message = "Mã tiền tệ không được để trống")
        private CurrencyCode currency;
        
        /**
         * URL để redirect sau khi thanh toán thành công (optional)
         * Nếu không có sẽ dùng returnUrl từ config
         */
        private String returnUrl;
        
        /**
         * URL để redirect khi hủy thanh toán (optional)
         * Nếu không có sẽ dùng cancelUrl từ config
         */
        private String cancelUrl;
        
        /**
         * Thông tin bổ sung cho đơn hàng (metadata)
         */
        private String orderInfo;
    }
    
    /**
     * Response DTO cho thanh toán
     * Trả về thông tin cần thiết cho mobile app
     */
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class PaymentResponse {
        
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
    
    /**
     * Response DTO cho trạng thái thanh toán
     * Trả về khi mobile app kiểm tra status
     */
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class PaymentStatusResponse {
        
        /**
         * ID giao dịch
         */
        private String transactionId;
        
        /**
         * Order code
         */
        private Long orderCode;
        
        /**
         * Trạng thái hiện tại
         */
        private TxnStatus status;
        
        /**
         * Số tiền
         */
        private Long amount;
        
        /**
         * Mô tả giao dịch
         */
        private String description;
        
        /**
         * Tên gateway (PayOS)
         */
        private String gatewayName;
        
        /**
         * Thông báo trạng thái
         */
        private String message;
        
        /**
         * Thời gian tạo
         */
        private String createdAt;
        
        /**
         * Thời gian cập nhật
         */
        private String updatedAt;
    }
}
