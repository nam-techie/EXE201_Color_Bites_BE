package com.exe201.color_bites_be.service;

import com.exe201.color_bites_be.dto.request.LoginRequest;
import com.exe201.color_bites_be.dto.request.RegisterRequest;
import com.exe201.color_bites_be.dto.response.AccountResponse;
import com.exe201.color_bites_be.entity.Account;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

/**
 * Interface định nghĩa các phương thức xác thực người dùng
 * Bao gồm đăng ký, đăng nhập, đăng xuất và quản lý tài khoản
 * Extends UserDetailsService để tương thích với Spring Security
 */
public interface IAuthenticationService extends UserDetailsService {
    
    /**
     * Đăng ký tài khoản mới
     * @param registerRequest Thông tin đăng ký
     * @return AccountResponse Thông tin tài khoản đã tạo
     */
    AccountResponse register(RegisterRequest registerRequest);
    
    /**
     * Đăng nhập vào hệ thống
     * @param loginRequest Thông tin đăng nhập
     * @return AccountResponse Thông tin tài khoản và token
     */
    AccountResponse login(LoginRequest loginRequest);
    
    /**
     * Đăng xuất khỏi hệ thống
     * @param token JWT token cần vô hiệu hóa
     */
    void logout(String token);
    
    /**
     * Cập nhật thông tin tài khoản với thời gian hiện tại
     * @param account Tài khoản cần cập nhật
     * @return Account Tài khoản đã được cập nhật
     */
    Account updateAccountWithCurrentTime(Account account);
    
    /**
     * Cập nhật thông tin tài khoản
     * @param accountId ID của tài khoản
     * @param updatedAccount Thông tin cập nhật
     * @return Account Tài khoản đã được cập nhật
     */
    Account updateAccount(String accountId, Account updatedAccount);
    
    /**
     * Upload ảnh đại diện cho người dùng
     * @param id ID của người dùng
     * @param file File ảnh cần upload
     * @return String URL của ảnh đã upload
     */
    String uploadImage(String id, MultipartFile file);
}
