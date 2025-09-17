package com.exe201.color_bites_be.controller;

import com.exe201.color_bites_be.dto.request.UserInformationRequest;
import com.exe201.color_bites_be.dto.response.ResponseDto;
import com.exe201.color_bites_be.dto.response.UserInformationResponse;
import com.exe201.color_bites_be.exception.DuplicateEntity;
import com.exe201.color_bites_be.exception.NotFoundException;
import com.exe201.color_bites_be.service.IUserInformationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize("hasAuthority('USER')")
@RequestMapping("/api/user-info")
public class UserInforController {

    @Autowired
    private IUserInformationService userInformationService;

    /**
     * Lấy thông tin user theo accountId
     */
    @GetMapping()
    public ResponseDto<UserInformationResponse> getUserInformation() {
        try {
            UserInformationResponse response = userInformationService.getUserInformation();
            return new ResponseDto<>(HttpStatus.OK.value(), "Lấy thông tin người dùng thành công", response);
        } catch (NotFoundException e) {
            return new ResponseDto<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi lấy thông tin người dùng", null);
        }
    }



    /**
     * Cập nhật thông tin user
     */
    @PutMapping()
    public ResponseDto<UserInformationResponse> updateUserInformation(
            @Valid @RequestBody UserInformationRequest request) {

        try {
            UserInformationResponse response = userInformationService.updateUserInformation(request);
            return new ResponseDto<>(HttpStatus.OK.value(), "Cập nhật thông tin người dùng thành công", response);
        } catch (NotFoundException e) {
            return new ResponseDto<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
        }catch (DuplicateEntity e) {
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi cập nhật thông tin người dùng", null);
        }
    }
}
