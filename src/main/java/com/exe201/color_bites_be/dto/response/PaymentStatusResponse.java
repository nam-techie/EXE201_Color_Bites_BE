package com.exe201.color_bites_be.dto.response;

import com.exe201.color_bites_be.enums.TxnStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response DTO cho trạng thái thanh toán
 * Dùng để mobile app kiểm tra status
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
     * Trạng thái thanh toán
     */
    private TxnStatus status;
    
    /**
     * Số tiền đã thanh toán
     */
    private Long amount;
    
    /**
     * Thời gian thanh toán (nếu thành công)
     */
    private String paidAt;
    
    /**
     * Thông tin bổ sung
     */
    private String description;
    
    /**
     * Gateway sử dụng
     */
    private String gatewayName;
    
    /**
     * Thông báo
     */
    private String message;
}
