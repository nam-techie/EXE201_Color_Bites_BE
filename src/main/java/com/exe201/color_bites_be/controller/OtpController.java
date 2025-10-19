package com.exe201.color_bites_be.controller;

import com.exe201.color_bites_be.dto.request.RegisterRequest;
import com.exe201.color_bites_be.dto.request.VerifyRegisterRequest;
import com.exe201.color_bites_be.dto.request.VerifyRequest;
import com.exe201.color_bites_be.dto.response.AccountResponse;
import com.exe201.color_bites_be.dto.response.ResponseDto;
import com.exe201.color_bites_be.exception.DuplicateEntity;
import com.exe201.color_bites_be.service.IAuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/otp")
@RequiredArgsConstructor
@Validated
public class OtpController {

    @Autowired
    IAuthenticationService authenticationService;

    @PostMapping("/verify-register")
    @Operation(summary = "Xác thực mã OTP đăng ký")
    public ResponseDto<AccountResponse> verifyRegister(@RequestBody @Valid VerifyRegisterRequest request) {
        try{
            AccountResponse accountResponse = authenticationService.verifyRegister(request);
            return new ResponseDto<AccountResponse>(HttpStatus.OK.value(), "Đăng ký thành công",
                    accountResponse);
        }catch (IllegalArgumentException e) {
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
        } catch (DuplicateEntity e) {
            // Xử lý lỗi duplicate với encoding đúng
            return new ResponseDto<>(HttpStatus.CONFLICT.value(), e.getMessage(), null);
        } catch (DataIntegrityViolationException e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi trong quá trình đăng ký, vui lòng thử lại sau.", null);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi không xác định, vui lòng thử lại sau.", null);
        }
    }

    @PostMapping("/verify-reset-password")
    @Operation(summary = "Xác thực mã OTP thay đổi mật khẩu")
    public ResponseDto<Object> verifyResetPassword(@RequestBody @Valid VerifyRequest request) {

        return ResponseDto.builder()
                .status(HttpStatus.OK.value())
                .data(authenticationService.verifyResetPassword(request))
                .message("Thay đổi mật khẩu thành công!")
                .build();
    }
}
