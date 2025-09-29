package com.exe201.color_bites_be.dto.request;

import com.exe201.color_bites_be.enums.CurrencyCode;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Request DTO để tạo thanh toán mới
 * Tuân thủ PayOS API specification
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreatePaymentRequest {
    
    
    /**
     * Số tiền thanh toán (VD: 200000 = 200,000 VND)
     */
    @NotNull(message = "Số tiền không được để trống")
    @DecimalMin(value = "1000", message = "Số tiền tối thiểu là 1,000 VND")
    private Long amount;
    
    /**
     * Mô tả đơn hàng (VD: "Thanh toán gói Premium 30 ngày")
     */
    @NotBlank(message = "Mô tả đơn hàng không được để trống")
    private String description;
    
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
     * Danh sách sản phẩm/dịch vụ (PayOS yêu cầu)
     */
    @NotNull(message = "Danh sách sản phẩm không được để trống")
    private List<PaymentItem> items;
    
    /**
     * Inner class cho Payment Item
     */
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PaymentItem {
        @NotBlank(message = "Tên sản phẩm không được để trống")
        private String name;
        
        @NotNull(message = "Số lượng không được để trống")
        @Positive(message = "Số lượng phải là số dương")
        private Integer quantity;
        
        @NotNull(message = "Giá sản phẩm không được để trống")
        @DecimalMin(value = "1000", message = "Giá sản phẩm tối thiểu là 1,000 VND")
        private Long price;
    }
}
