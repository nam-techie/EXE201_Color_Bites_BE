package com.exe201.color_bites_be.dto.response;

import com.exe201.color_bites_be.enums.TransactionEnums.TxnStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response DTO cho trạng thái thanh toán
 * Trả về khi mobile app kiểm tra status
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentStatusResponse {
    
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
