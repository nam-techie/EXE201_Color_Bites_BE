package com.exe201.color_bites_be.service;

import com.exe201.color_bites_be.dto.response.ListAccountResponse;

import java.util.List;

/**
 * Interface định nghĩa các phương thức quản trị hệ thống
 * Chỉ dành cho admin
 */
public interface IAdminService {
    
    /**
     * Lấy danh sách tất cả người dùng (chỉ admin)
     */
    List<ListAccountResponse> getAllUserByAdmin();
    
    /**
     * Chặn người dùng
     */
    void blockUser(String accountId);
    
    /**
     * Kích hoạt lại người dùng
     */
    void activeUser(String accountId);
}
