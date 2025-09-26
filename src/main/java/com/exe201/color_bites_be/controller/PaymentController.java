package com.exe201.color_bites_be.controller;

import com.exe201.color_bites_be.dto.request.CreatePaymentRequest;
import com.exe201.color_bites_be.dto.response.PaymentResponse;
import com.exe201.color_bites_be.dto.response.PaymentStatusResponse;
import com.exe201.color_bites_be.dto.response.ResponseDto;
import com.exe201.color_bites_be.entity.Transaction;
import com.exe201.color_bites_be.service.IPaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller xử lý thanh toán cho mobile app
 * Tích hợp với flow hệ thống mua gói subscription
 */
@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Payment Management", description = "API quản lý thanh toán subscription cho mobile app")
@SecurityRequirement(name = "bearerAuth")
public class PaymentController {
    
    private final IPaymentService paymentService;
    
    @Operation(
        summary = "Tạo thanh toán subscription",
        description = "Tạo thanh toán để mua gói PREMIUM. Sau khi thanh toán thành công, user sẽ được nâng cấp lên PREMIUM."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tạo thanh toán thành công"),
        @ApiResponse(responseCode = "400", description = "Dữ liệu đầu vào không hợp lệ"),
        @ApiResponse(responseCode = "401", description = "Chưa xác thực"),
        @ApiResponse(responseCode = "500", description = "Lỗi hệ thống")
    })
    @PostMapping("/subscription/create")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseDto<PaymentResponse>> createSubscriptionPayment(
            @Valid @RequestBody CreatePaymentRequest request,
            Authentication authentication) {
        
        try {
            log.info("Tạo thanh toán subscription cho user: {}", authentication.getName());
            
            PaymentResponse response = paymentService.createSubscriptionPayment(request, authentication.getName());
            
            return ResponseEntity.ok(ResponseDto.<PaymentResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Tạo thanh toán subscription thành công")
                .data(response)
                .build());
            
        } catch (Exception e) {
            log.error("Lỗi tạo thanh toán subscription: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseDto.<PaymentResponse>builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Lỗi hệ thống: " + e.getMessage())
                    .build());
        }
    }
    
    @Operation(
        summary = "Kiểm tra trạng thái thanh toán",
        description = "Mobile app gọi để kiểm tra trạng thái thanh toán theo transaction ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lấy trạng thái thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy giao dịch"),
        @ApiResponse(responseCode = "401", description = "Chưa xác thực")
    })
    @GetMapping("/status/{transactionId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseDto<PaymentStatusResponse>> checkPaymentStatus(
            @Parameter(description = "ID giao dịch cần kiểm tra")
            @PathVariable String transactionId,
            Authentication authentication) {
        
        try {
            log.info("Kiểm tra trạng thái thanh toán: {} cho user: {}", transactionId, authentication.getName());
            
            PaymentStatusResponse response = paymentService.checkPaymentStatus(transactionId, authentication.getName());
            
            return ResponseEntity.ok(ResponseDto.<PaymentStatusResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Lấy trạng thái thanh toán thành công")
                .data(response)
                .build());
                
        } catch (Exception e) {
            log.error("Lỗi kiểm tra trạng thái thanh toán: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseDto.<PaymentStatusResponse>builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Lỗi hệ thống: " + e.getMessage())
                    .build());
        }
    }
    
    @Operation(
        summary = "Webhook callback từ PayOS",
        description = "Endpoint để PayOS gửi callback khi có thay đổi trạng thái thanh toán"
    )
    @PostMapping("/payos/webhook")
    public ResponseEntity<Map<String, String>> handlePayOSWebhook(
            @RequestBody Map<String, String> callbackData) {
        
        try {
            log.info("Nhận webhook từ PayOS: {}", callbackData);
            
            boolean success = paymentService.handlePaymentCallback(callbackData);
            
            if (success) {
                return ResponseEntity.ok(Map.of(
                    "code", "00",
                    "desc", "success"
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                    "code", "01", 
                    "desc", "Callback processing failed"
                ));
            }
            
        } catch (Exception e) {
            log.error("Lỗi xử lý webhook PayOS: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "code", "99",
                    "desc", "Internal server error"
                ));
        }
    }
    
    @Operation(
        summary = "Return URL sau thanh toán",
        description = "Endpoint để PayOS redirect sau khi user hoàn thành thanh toán"
    )
    @GetMapping("/payos/return")
    public ResponseEntity<String> handlePayOSReturn(
            @RequestParam Map<String, String> params) {
        
        try {
            log.info("PayOS return callback: {}", params);
            
            String orderCode = params.get("orderCode");
            String status = params.get("status");
            
            // Xử lý return logic tùy theo yêu cầu
            // Có thể redirect về mobile app hoặc show success page
            
            if ("SUCCESS".equals(status)) {
                return ResponseEntity.ok("""
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
                    """.formatted(orderCode));
            } else {
                return ResponseEntity.ok("""
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
                    """);
            }
            
        } catch (Exception e) {
            log.error("Lỗi xử lý PayOS return: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Lỗi hệ thống");
        }
    }
    
    @Operation(
        summary = "Lấy lịch sử giao dịch",
        description = "Lấy danh sách tất cả giao dịch của user"
    )
    @GetMapping("/history")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseDto<List<Transaction>>> getTransactionHistory(
            Authentication authentication) {
        
        try {
            log.info("Lấy lịch sử giao dịch cho user: {}", authentication.getName());
            
            List<Transaction> transactions = paymentService.getTransactionHistory(authentication.getName());
            
            return ResponseEntity.ok(ResponseDto.<List<Transaction>>builder()
                .status(HttpStatus.OK.value())
                .message("Lấy lịch sử giao dịch thành công")
                .data(transactions)
                .build());
                
        } catch (Exception e) {
            log.error("Lỗi lấy lịch sử giao dịch: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseDto.<List<Transaction>>builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Lỗi hệ thống: " + e.getMessage())
                    .build());
        }
    }
}
