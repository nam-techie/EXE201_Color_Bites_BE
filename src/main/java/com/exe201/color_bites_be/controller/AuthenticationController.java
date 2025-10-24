package com.exe201.color_bites_be.controller;

import com.exe201.color_bites_be.dto.request.ForgotPasswordRequest;
import com.exe201.color_bites_be.dto.request.ChangePasswordRequest;
import com.exe201.color_bites_be.dto.request.LoginRequest;
import com.exe201.color_bites_be.dto.request.RegisterRequest;
import com.exe201.color_bites_be.dto.response.AccountResponse;
import com.exe201.color_bites_be.dto.response.ResponseDto;
import com.exe201.color_bites_be.exception.DisabledException;
import com.exe201.color_bites_be.exception.DuplicateEntity;
import com.exe201.color_bites_be.exception.NotFoundException;
import com.exe201.color_bites_be.service.IAuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Autowired
    IAuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseDto<String> register(@Valid @RequestBody RegisterRequest account) {
        authenticationService.register(account);
        return new ResponseDto<String>(HttpStatus.OK.value(), "Vui lòng kiểm tra email để lấy mã OTP",
                null);
    }

    @PostMapping("/login")
    public ResponseDto<AccountResponse> login(@RequestBody LoginRequest request) {
        try {
            AccountResponse accountResponse = authenticationService.login(request);
            return new ResponseDto<>(HttpStatus.OK.value(), "Đăng nhập thành công", accountResponse);
        } catch (DisabledException e) {
            return new ResponseDto<>(HttpStatus.FORBIDDEN.value(), e.getMessage(), null);
        } catch (RuntimeException e) {
            return new ResponseDto<>(HttpStatus.UNAUTHORIZED.value(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null);
        }
    }

    @PostMapping("/logout")
    public ResponseDto<String> logout(@RequestHeader("Authorization") String token) {
        // Cắt bỏ tiền tố "Bearer " nếu token có tiền tố
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        authenticationService.logout(token);
        return ResponseDto.<String>builder()
                .status(HttpStatus.OK.value())
                .message("Đăng xuất thành công")
                .build();
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Quên mật khẩu và gửi mã OTP về email")
    public ResponseDto<Object> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        // Luôn trả về thông điệp chung để tránh lộ việc email có tồn tại hay không
        try {
            authenticationService.forgotPassword(request);
        } catch (Exception ignored) {
            // Không trả về chi tiết lỗi để tránh email enumeration
        }
        return ResponseDto.builder()
                .status(HttpStatus.OK.value())
                .message("Nếu email tồn tại, chúng tôi đã gửi OTP hướng dẫn đặt lại mật khẩu")
                .build();
    }

    @PutMapping("/change-password")
    @PreAuthorize("hasAuthority('USER')")
    @Operation(summary = "Đổi mật khẩu chủ động (đã đăng nhập)")
    public ResponseDto<Object> changePassword(@RequestBody @Valid ChangePasswordRequest request) {
        authenticationService.changePassword(request);
        return ResponseDto.builder()
                .status(HttpStatus.OK.value())
                .message("Đổi mật khẩu thành công")
                .build();
    }


//
//    @PostMapping("/uploadVideo/{id}")
//    public ResponseEntity uploadVideo(@PathVariable String id,@RequestPart MultipartFile file) {
//        authenticationService.uploadVideo(id,file);
//        return ResponseEntity.ok("Uploaded video successfully");
//    }
}
