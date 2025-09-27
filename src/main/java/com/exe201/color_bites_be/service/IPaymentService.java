package com.exe201.color_bites_be.service;

import com.exe201.color_bites_be.dto.request.CreatePaymentRequest;
import com.exe201.color_bites_be.dto.request.PayOSWebhookRequest;
import com.exe201.color_bites_be.dto.response.PaymentResponse;
import com.exe201.color_bites_be.dto.response.PaymentStatusResponse;
import com.exe201.color_bites_be.dto.response.PayOSWebhookResponse;
import com.exe201.color_bites_be.entity.Transaction;

import java.util.Map;

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
     * Xử lý webhook callback từ PayOS
     */
    PayOSWebhookResponse handlePayOSWebhook(PayOSWebhookRequest request);
    
    /**
     * Confirm payment status từ FE (an toàn - gọi PayOS để verify)
     */
    PaymentStatusResponse updateStatusFromGateway(String id);
    
    /**
     * Lấy lịch sử giao dịch của user
     */
    java.util.List<Transaction> getTransactionHistory(String accountId);
    
    /**
     * Xử lý thanh toán thành công - upgrade subscription
     */
    void processSuccessfulPayment(Transaction transaction);
    
    /**
     * Xử lý thanh toán thất bại
     */
    void processFailedPayment(Transaction transaction);
}