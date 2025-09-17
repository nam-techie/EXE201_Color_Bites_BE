package com.exe201.color_bites_be.service;

import java.util.Map;

/**
 * Interface định nghĩa các phương thức xử lý thanh toán
 * Hỗ trợ nhiều cổng thanh toán khác nhau (Strategy Pattern)
 */
public interface IPaymentGateway {
    
    /**
     * Tạo thanh toán mới
     * @param orderInfo Thông tin đơn hàng
     * @return PaymentInitResponse URL thanh toán và thông tin liên quan
     */
    PaymentInitResponse createPayment(OrderInfo orderInfo);
    
    /**
     * Xử lý callback từ cổng thanh toán
     * @param params Tham số từ callback
     * @return PaymentCallbackResult Kết quả xử lý
     */
    PaymentCallbackResult handleCallback(Map<String, String> params);
    
    /**
     * Kiểm tra trạng thái thanh toán
     * @param transactionId ID giao dịch
     * @return PaymentStatus Trạng thái thanh toán
     */
    PaymentStatus checkPaymentStatus(String transactionId);
    
    /**
     * Lấy tên cổng thanh toán
     */
    String getGatewayName();
    
    // TODO: Define these DTOs when implementing payment feature
    interface PaymentInitResponse {
        String getPaymentUrl();
        String getTransactionId();
        String getStatus();
    }
    
    interface PaymentCallbackResult {
        boolean isSuccess();
        String getTransactionId();
        String getMessage();
    }
    
    interface OrderInfo {
        String getOrderId();
        long getAmount();
        String getCurrency();
        String getDescription();
        String getCustomerInfo();
    }
    
    enum PaymentStatus {
        PENDING, SUCCESS, FAILED, CANCELLED
    }
}
