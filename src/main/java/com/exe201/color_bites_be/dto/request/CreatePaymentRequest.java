package com.exe201.color_bites_be.dto.request;

import com.exe201.color_bites_be.enums.CurrencyCode;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request DTO để tạo thanh toán mới
 * Chứa thông tin cần thiết cho mobile app gửi lên
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreatePaymentRequest {
    
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
