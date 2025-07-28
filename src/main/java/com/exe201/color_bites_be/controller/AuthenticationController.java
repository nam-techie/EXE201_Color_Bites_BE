package com.exe201.color_bites_be.controller;

import com.exe201.color_bites_be.dto.request.LoginRequest;
import com.exe201.color_bites_be.dto.request.RegisterRequest;
import com.exe201.color_bites_be.dto.response.AccountResponse;
import com.exe201.color_bites_be.dto.response.ResponseDto;
import com.exe201.color_bites_be.exception.DuplicateEntity;
import com.exe201.color_bites_be.exception.NotFoundException;
import com.exe201.color_bites_be.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Autowired
    AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseDto<String> register(@Valid @RequestBody RegisterRequest account, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errors = new StringBuilder();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.append(error.getDefaultMessage()).append(". ");
            }
            return new ResponseDto<>(
                    HttpStatus.BAD_REQUEST.value(),
                    "Validation failed",
                    errors.toString());
        }
        try {
            authenticationService.register(account);
            return new ResponseDto<String>(HttpStatus.OK.value(), "Đăng ký thành công",
                    "Vui lòng đăng nhập để tiếp tục");
        } catch (IllegalArgumentException e) {
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

    @PostMapping("/login")
    public ResponseDto<AccountResponse> login(@RequestBody LoginRequest request) {
        try {
            AccountResponse accountResponse = authenticationService.login(request);
            return new ResponseDto<>(HttpStatus.OK.value(), "Đăng nhập thành công", accountResponse);
        } catch (NotFoundException e) {
            return new ResponseDto<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
        } catch (RuntimeException e) {
            return new ResponseDto<>(HttpStatus.UNAUTHORIZED.value(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        // Cắt bỏ tiền tố "Bearer " nếu token có tiền tố
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        authenticationService.logout(token);
        return ResponseEntity.ok("Đăng xuất thành công.");
    }
}
