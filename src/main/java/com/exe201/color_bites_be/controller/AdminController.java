package com.exe201.color_bites_be.controller;

import com.exe201.color_bites_be.dto.response.ListAccountResponse;
import com.exe201.color_bites_be.dto.response.ResponseDto;
import com.exe201.color_bites_be.service.IAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController {
    @Autowired
    IAdminService adminService;

    @GetMapping("/user")
    public ResponseDto<List<ListAccountResponse>> getAllUserByAdmin(){
        List<ListAccountResponse> listAccountResponses = adminService.getAllUserByAdmin();
        return new ResponseDto<>(HttpStatus.OK.value(),"successfully", listAccountResponses);
    }

    @PutMapping("/block-user/{id}")
    public ResponseDto<Void> blockUser(@PathVariable String id) {
        adminService.blockUser(id);
        return new ResponseDto<>(HttpStatus.OK.value(), "Chặn người dùng thành công", null);
    }

    @PutMapping("/active-user/{id}")
    public ResponseDto<Void> activeUser(@PathVariable String id) {
        adminService.activeUser(id);
        return new ResponseDto<>(HttpStatus.OK.value(), "Người dùng đã được kích hoạt", null);
    }

}
