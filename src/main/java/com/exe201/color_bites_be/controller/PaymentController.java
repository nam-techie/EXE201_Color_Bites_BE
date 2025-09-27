package com.exe201.color_bites_be.controller;

import com.exe201.color_bites_be.dto.request.CreatePaymentRequest;
import com.exe201.color_bites_be.dto.response.PaymentResponse;
import com.exe201.color_bites_be.dto.response.PaymentStatusResponse;
import com.exe201.color_bites_be.dto.response.ResponseDto;
import com.exe201.color_bites_be.entity.Transaction;
import com.exe201.color_bites_be.exception.NotFoundException;
import com.exe201.color_bites_be.service.IPaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
     * Kiểm tra trạng thái thanh toán
     */
    @GetMapping("/status/{transactionId}")
    public ResponseDto<PaymentStatusResponse> checkPaymentStatus(
            @PathVariable String transactionId,
            Authentication authentication) {
        
        try {
            PaymentStatusResponse response = paymentService.checkPaymentStatus(transactionId, authentication.getName());
            return new ResponseDto<>(HttpStatus.OK.value(), "Lấy trạng thái thanh toán thành công", response);
        } catch (NotFoundException e) {
            return new ResponseDto<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi kiểm tra trạng thái thanh toán: " + e.getMessage(), null);
        }
    }
    
    /**
     * Webhook callback từ PayOS
     */
    @PostMapping("/payos/webhook")
    public Map<String, String> handlePayOSWebhook(
            @RequestBody Map<String, String> callbackData) {
        
        try {
            boolean success = paymentService.handlePaymentCallback(callbackData);
            
            if (success) {
                return Map.of("code", "00", "desc", "success");
            } else {
                return Map.of("code", "01", "desc", "Callback processing failed");
            }
            
        } catch (Exception e) {
            return Map.of("code", "99", "desc", "Internal server error");
        }
    }
    
    /**
     * Return URL sau thanh toán
     */
    @GetMapping("/payos/return")
    public String handlePayOSReturn(
            @RequestParam Map<String, String> params) {
        
        try {
            String orderCode = params.get("orderCode");
            String status = params.get("status");
            
            if ("SUCCESS".equals(status)) {
                return """
                    <html>
                    <body>
                        <h2>Thanh toán thành công!</h2>
                        <p>Mã đơn hàng: %s</p>
                        <p>Bạn có thể đóng trang này và quay lại ứng dụng.</p>
                        <script>
                            setTimeout(function() {
                                window.close();
                            }, 3000);
                        </script>
                    </body>
                    </html>
                    """.formatted(orderCode);
            } else {
                return """
                    <html>
                    <body>
                        <h2>Thanh toán thất bại!</h2>
                        <p>Vui lòng thử lại sau.</p>
                        <script>
                            setTimeout(function() {
                                window.close();
                            }, 3000);
                        </script>
                    </body>
                    </html>
                    """;
            }
            
        } catch (Exception e) {
            return "Lỗi hệ thống";
        }
    }
    
    /**
     * Lấy lịch sử giao dịch
     */
    @GetMapping("/history")
    public ResponseDto<List<Transaction>> getTransactionHistory(
            Authentication authentication) {
        
        try {
            List<Transaction> transactions = paymentService.getTransactionHistory(authentication.getName());
            return new ResponseDto<>(HttpStatus.OK.value(), "Lấy lịch sử giao dịch thành công", transactions);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi lấy lịch sử giao dịch: " + e.getMessage(), null);
        }
    }
}
