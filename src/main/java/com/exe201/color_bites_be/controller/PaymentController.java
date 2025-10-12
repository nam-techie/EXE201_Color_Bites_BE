package com.exe201.color_bites_be.controller;

import com.exe201.color_bites_be.dto.request.CreatePaymentRequest;
import com.exe201.color_bites_be.dto.request.PayOSWebhookRequest;
import com.exe201.color_bites_be.dto.response.PaymentResponse;
import com.exe201.color_bites_be.dto.response.PaymentStatusResponse;
import com.exe201.color_bites_be.dto.response.PayOSWebhookResponse;
import com.exe201.color_bites_be.dto.response.ResponseDto;
import com.exe201.color_bites_be.service.IPaymentService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/payment")
@Slf4j
public class PaymentController {
    
    @Autowired
    private IPaymentService paymentService;
    
    /**
     * Tạo thanh toán subscription
     * Yêu cầu authentication (USER role)
     */
    @PostMapping("/subscription/create")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseDto<PaymentResponse> createSubscriptionPayment(
            @Valid @RequestBody CreatePaymentRequest request) {
        
        try {
            PaymentResponse response = paymentService.createSubscriptionPayment(request);
            return new ResponseDto<>(HttpStatus.OK.value(), "Tạo thanh toán subscription thành công", response);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi tạo thanh toán subscription: " + e.getMessage(), null);
        }
    }
    
    
    
    /**
     * Confirm payment status từ FE (an toàn - gọi PayOS để verify)
     * Yêu cầu authentication (USER role)
     */
    @GetMapping("/confirm")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseDto<PaymentStatusResponse> confirmPayment(@RequestParam("id") String id) {
        try {
            PaymentStatusResponse response = paymentService.updateStatusFromGateway(id);
            return new ResponseDto<>(HttpStatus.OK.value(), "Xác nhận thanh toán thành công", response);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Lỗi xác nhận thanh toán: " + e.getMessage(), null);
        }
    }
    
    /**
     * Webhook callback từ PayOS (PUBLIC endpoint - no authentication required)
     * PayOS sẽ gọi endpoint này khi có thay đổi trạng thái thanh toán
     * Security: Verify signature trong service layer
     */
    @PostMapping("/payos/webhook")
    public PayOSWebhookResponse handlePayOSWebhook(@RequestBody PayOSWebhookRequest request) {
        try {
            log.info("=== NHẬN WEBHOOK TỪ PAYOS ===");
            log.info("OrderCode: {}", request.getData() != null ? request.getData().getOrderCode() : "null");
            log.info("Code: {}", request.getCode());
            log.info("Signature: {}", request.getSignature());
            
            // Gọi service để xử lý webhook
            PayOSWebhookResponse response = paymentService.handlePayOSWebhook(request);
            
            log.info("Webhook processed - Response code: {}", response.getCode());
            return response;
            
        } catch (Exception e) {
            log.error("Lỗi xử lý webhook PayOS: ", e);
            return PayOSWebhookResponse.error("Internal server error: " + e.getMessage());
        }
    }
    
}