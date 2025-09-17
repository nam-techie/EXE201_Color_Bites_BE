package com.exe201.color_bites_be.service;

import com.exe201.color_bites_be.entity.Account;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;


/**
 * Interface định nghĩa các phương thức xử lý JWT token
 * Bao gồm tạo token, validate, blacklist và extract thông tin
 */
public interface ITokenService {
    
    /**
     * Tạo JWT token mới cho account
     * @param account Tài khoản cần tạo token
     * @return String JWT token
     */
    String generateToken(Account account);
    
    /**
     * Đưa token vào danh sách đen (vô hiệu hóa token)
     * @param token JWT token cần vô hiệu hóa
     */
    void invalidateToken(String token);
    
    /**
     * Lấy thông tin Account từ token
     * @param token JWT token
     * @return Account Thông tin tài khoản
     */
    Account getAccountByToken(String token);
    
    /**
     * Kiểm tra token có bị blacklist không
     * @param token JWT token cần kiểm tra
     * @return boolean True nếu token bị blacklist
     */
    boolean isTokenBlacklisted(String token);
    
    /**
     * Lấy token từ Authorization header
     * @param authHeader Authorization header
     * @return String JWT token (không có Bearer prefix)
     */
    String getToken(String authHeader);
    
    /**
     * Extract username từ token
     * @param token JWT token
     * @return String Username
     */
    String extractUsername(String token);
    
    /**
     * Extract claims từ token
     * @param token JWT token
     * @param claimsResolver Function để resolve claims
     * @return T Kết quả sau khi resolve
     */
    <T> T extractClaims(String token, java.util.function.Function<Claims, T> claimsResolver);
    
    /**
     * Extract tất cả claims từ token
     * @param token JWT token
     * @return Claims Tất cả claims trong token
     */
    Claims extractAllClaims(String token);
    
    /**
     * Validate token với user details
     * @param token JWT token
     * @param userDetails User details để validate
     * @return boolean True nếu token hợp lệ
     */
    boolean validateToken(String token, UserDetails userDetails);
}
