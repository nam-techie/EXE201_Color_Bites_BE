package com.exe201.color_bites_be.controller;

import com.exe201.color_bites_be.dto.request.CreatePaymentRequest;
import com.exe201.color_bites_be.dto.response.PaymentResponse;
import com.exe201.color_bites_be.dto.response.PaymentStatusResponse;
import com.exe201.color_bites_be.dto.response.ResponseDto;
import com.exe201.color_bites_be.service.IPaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@PreAuthorize("hasAuthority('USER')")
@RequestMapping("/api/payment")
public class PaymentController {
    
    @Autowired
    private IPaymentService paymentService;
    
    /**
     * Tạo thanh toán subscription
     */
    @PostMapping("/subscription/create")
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
     */
    @GetMapping("/confirm")
    public ResponseDto<PaymentStatusResponse> confirmPayment(@RequestParam("id") String id) {
        try {
            PaymentStatusResponse response = paymentService.updateStatusFromGateway(id);
            return new ResponseDto<>(HttpStatus.OK.value(), "Xác nhận thanh toán thành công", response);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Lỗi xác nhận thanh toán: " + e.getMessage(), null);
        }
    }
    
}