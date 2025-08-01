package com.exe201.color_bites_be.controller;

import com.exe201.color_bites_be.dto.request.UserInformationRequest;
import com.exe201.color_bites_be.dto.response.ResponseDto;
import com.exe201.color_bites_be.dto.response.UserInformationResponse;
import com.exe201.color_bites_be.exception.NotFoundException;
import com.exe201.color_bites_be.service.UserInformationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize("hasRole('USER')")
@RequestMapping("/api/user-info")
public class UserInforController {

    @Autowired
    private UserInformationService userInformationService;

    /**
     * Lấy thông tin user theo accountId
     */
    @GetMapping("/{accountId}")
    public ResponseDto<UserInformationResponse> getUserInformation(@PathVariable String accountId) {
        try {
            UserInformationResponse response = userInformationService.getUserInformation(accountId);
            return new ResponseDto<>(HttpStatus.OK.value(), "Lấy thông tin người dùng thành công", response);
        } catch (NotFoundException e) {
            return new ResponseDto<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi lấy thông tin người dùng", null);
        }
    }

    /**
     * Tạo hoặc cập nhật thông tin user lần đầu
     */
    @PostMapping("/{accountId}")
    public ResponseDto<UserInformationResponse> createOrUpdateUserInformation(
            @PathVariable String accountId,
            @Valid @RequestBody UserInformationRequest request,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            StringBuilder errors = new StringBuilder();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.append(error.getDefaultMessage()).append(". ");
            }
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), "Validation failed", null);
        }

        try {
            UserInformationResponse response = userInformationService.createOrUpdateUserInformation(accountId, request);
            return new ResponseDto<>(HttpStatus.OK.value(), "Cập nhật thông tin người dùng thành công", response);
        } catch (NotFoundException e) {
            return new ResponseDto<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi cập nhật thông tin người dùng", null);
        }
    }

    /**
     * Cập nhật thông tin user
     */
    @PutMapping("/{accountId}")
    public ResponseDto<UserInformationResponse> updateUserInformation(
            @PathVariable String accountId,
            @Valid @RequestBody UserInformationRequest request,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            StringBuilder errors = new StringBuilder();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.append(error.getDefaultMessage()).append(". ");
            }
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), "Validation failed", null);
        }

        try {
            UserInformationResponse response = userInformationService.updateUserInformation(accountId, request);
            return new ResponseDto<>(HttpStatus.OK.value(), "Cập nhật thông tin người dùng thành công", response);
        } catch (NotFoundException e) {
            return new ResponseDto<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi cập nhật thông tin người dùng", null);
        }
    }
}
