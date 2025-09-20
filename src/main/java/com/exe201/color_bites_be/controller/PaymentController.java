package com.exe201.color_bites_be.controller;

import com.exe201.color_bites_be.dto.request.CreatePaymentRequest;
import com.exe201.color_bites_be.dto.response.PaymentResponse;
import com.exe201.color_bites_be.dto.response.PaymentStatusResponse;
import com.exe201.color_bites_be.dto.response.ResponseDto;
import com.exe201.color_bites_be.service.IPaymentGateway;
import com.exe201.color_bites_be.service.impl.PayOSGateway;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Controller xử lý thanh toán cho mobile app
 * Tích hợp với PayOS payment gateway
 */
@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Payment Management", description = "API quản lý thanh toán cho mobile app")
@SecurityRequirement(name = "bearerAuth")
public class PaymentController {
    
    private final PayOSGateway payOSGateway;
    
    @Operation(
        summary = "Tạo thanh toán mới",
        description = "Tạo link thanh toán PayOS cho mobile app. Mobile sẽ nhận được URL để redirect user."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tạo thanh toán thành công"),
        @ApiResponse(responseCode = "400", description = "Dữ liệu đầu vào không hợp lệ"),
        @ApiResponse(responseCode = "401", description = "Chưa xác thực"),
        @ApiResponse(responseCode = "500", description = "Lỗi hệ thống")
    })
    @PostMapping("/create")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseDto<PaymentResponse>> createPayment(
            @Valid @RequestBody CreatePaymentRequest request,
            Authentication authentication) {
        
        try {
            log.info("Tạo thanh toán cho user: {}", authentication.getName());
            
            // Tạo OrderInfo từ request
            IPaymentGateway.OrderInfo orderInfo = new OrderInfoImpl(
                generateOrderId(),
                request.getAmount(),
                request.getCurrency().name(),
                request.getDescription(),
                "accountId:" + authentication.getName()
            );
            
            // Gọi PayOS để tạo thanh toán
            IPaymentGateway.PaymentInitResponse paymentResponse = payOSGateway.createPayment(orderInfo);
            
            if ("SUCCESS".equals(paymentResponse.getStatus())) {
                PaymentResponse response = PaymentResponse.builder()
                    .checkoutUrl(paymentResponse.getPaymentUrl())
                    .paymentLinkId(paymentResponse.getTransactionId())
                    .orderCode(Long.valueOf(paymentResponse.getTransactionId()))
                    .status("SUCCESS")
                    .createdAt(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    .message("Tạo thanh toán thành công")
                    .build();
                
                return ResponseEntity.ok(ResponseDto.<PaymentResponse>builder()
                    .status(HttpStatus.OK.value())
                    .message("Tạo thanh toán thành công")
                    .data(response)
                    .build());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseDto.<PaymentResponse>builder()
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .message("Không thể tạo thanh toán. Vui lòng thử lại sau.")
                        .build());
            }
            
        } catch (Exception e) {
            log.error("Lỗi tạo thanh toán: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseDto.<PaymentResponse>builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Lỗi hệ thống khi tạo thanh toán")
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
            
            IPaymentGateway.PaymentStatus status = payOSGateway.checkPaymentStatus(transactionId);
            
            PaymentStatusResponse response = PaymentStatusResponse.builder()
                .transactionId(transactionId)
                .status(mapPaymentStatusToTxnStatus(status))
                .gatewayName(payOSGateway.getGatewayName())
                .message(getStatusMessage(status))
                .build();
            
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
                    .message("Lỗi hệ thống khi kiểm tra trạng thái")
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
            
            IPaymentGateway.PaymentCallbackResult result = payOSGateway.handleCallback(callbackData);
            
            if (result.isSuccess()) {
                return ResponseEntity.ok(Map.of(
                    "code", "00",
                    "desc", "success"
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                    "code", "01", 
                    "desc", result.getMessage()
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
    
    /**
     * Generate unique order ID
     */
    private String generateOrderId() {
        return "ORDER_" + System.currentTimeMillis();
    }
    
    /**
     * Map PaymentStatus sang TxnStatus
     */
    private com.exe201.color_bites_be.enums.TxnStatus mapPaymentStatusToTxnStatus(IPaymentGateway.PaymentStatus status) {
        return switch (status) {
            case SUCCESS -> com.exe201.color_bites_be.enums.TxnStatus.SUCCESS;
            case PENDING -> com.exe201.color_bites_be.enums.TxnStatus.PENDING;
            case CANCELLED -> com.exe201.color_bites_be.enums.TxnStatus.CANCELED;
            case FAILED -> com.exe201.color_bites_be.enums.TxnStatus.FAILED;
        };
    }
    
    /**
     * Lấy message theo status
     */
    private String getStatusMessage(IPaymentGateway.PaymentStatus status) {
        return switch (status) {
            case SUCCESS -> "Thanh toán thành công";
            case PENDING -> "Đang chờ thanh toán";
            case CANCELLED -> "Thanh toán đã bị hủy";
            case FAILED -> "Thanh toán thất bại";
        };
    }
    
    /**
     * Implementation của OrderInfo interface
     */
    private record OrderInfoImpl(String orderId, Long amount, String currency, String description, String customerInfo)
            implements IPaymentGateway.OrderInfo {
        @Override
        public String getOrderId() { return orderId; }
        @Override
        public long getAmount() { return amount; }
        @Override
        public String getCurrency() { return currency; }
        @Override
        public String getDescription() { return description; }
        @Override
        public String getCustomerInfo() { return customerInfo; }
    }
}
