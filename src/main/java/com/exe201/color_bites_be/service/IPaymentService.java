package com.exe201.color_bites_be.service;

import com.exe201.color_bites_be.dto.request.CreatePaymentRequest;
import com.exe201.color_bites_be.dto.response.PaymentResponse;
import com.exe201.color_bites_be.dto.response.PaymentStatusResponse;
import com.exe201.color_bites_be.entity.Transaction;

/**
 * Interface định nghĩa các phương thức xử lý thanh toán
 * Tích hợp với flow hệ thống mua gói
 */
public interface IPaymentService {
    
    /**
     * Tạo thanh toán mới cho subscription
     */
    PaymentResponse createSubscriptionPayment(CreatePaymentRequest request);
    
    /**
     * Confirm payment status từ FE (an toàn - gọi PayOS để verify)
     */
    PaymentStatusResponse updateStatusFromGateway(String id);
    
    /**
     * Xử lý thanh toán thành công - upgrade subscription
     */
    void processSuccessfulPayment(Transaction transaction);
    
    /**
     * Xử lý thanh toán thất bại
     */
    void processFailedPayment(Transaction transaction);
}