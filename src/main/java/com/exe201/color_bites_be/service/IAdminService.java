package com.exe201.color_bites_be.service;

import com.exe201.color_bites_be.dto.response.ListAccountResponse;
import com.exe201.color_bites_be.dto.response.AdminPostResponse;
import com.exe201.color_bites_be.dto.response.AdminRestaurantResponse;
import com.exe201.color_bites_be.dto.response.AdminTransactionResponse;
import com.exe201.color_bites_be.dto.response.AdminCommentResponse;
import com.exe201.color_bites_be.dto.response.AdminTagResponse;
import com.exe201.color_bites_be.dto.response.StatisticsResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * Interface định nghĩa các phương thức quản trị hệ thống
 * Chỉ dành cho admin
 */
public interface IAdminService {
    
    // ========== USER MANAGEMENT ==========
    
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
    
    // ========== POST MANAGEMENT ==========
    
    /**
     * Lấy danh sách tất cả bài viết (admin view)
     */
    Page<AdminPostResponse> getAllPostsByAdmin(int page, int size);
    
    /**
     * Lấy bài viết theo ID (admin view)
     */
    AdminPostResponse getPostByIdByAdmin(String postId);
    
    /**
     * Xóa bài viết (admin)
     */
    void deletePostByAdmin(String postId);
    
    /**
     * Khôi phục bài viết đã xóa (admin)
     */
    void restorePostByAdmin(String postId);
    
    // ========== RESTAURANT MANAGEMENT ==========
    
    /**
     * Lấy danh sách tất cả nhà hàng (admin view)
     */
    Page<AdminRestaurantResponse> getAllRestaurantsByAdmin(int page, int size);
    
    /**
     * Lấy nhà hàng theo ID (admin view)
     */
    AdminRestaurantResponse getRestaurantByIdByAdmin(String restaurantId);
    
    /**
     * Xóa nhà hàng (admin)
     */
    void deleteRestaurantByAdmin(String restaurantId);
    
    /**
     * Khôi phục nhà hàng đã xóa (admin)
     */
    void restoreRestaurantByAdmin(String restaurantId);
    
    // ========== TRANSACTION MANAGEMENT ==========
    
    /**
     * Lấy danh sách tất cả giao dịch (admin view)
     */
    Page<AdminTransactionResponse> getAllTransactionsByAdmin(int page, int size);
    
    /**
     * Lấy giao dịch theo ID (admin view)
     */
    AdminTransactionResponse getTransactionByIdByAdmin(String transactionId);
    
    /**
     * Lấy giao dịch theo trạng thái (admin view)
     */
    Page<AdminTransactionResponse> getTransactionsByStatusByAdmin(String status, int page, int size);
    
    // ========== COMMENT MANAGEMENT ==========
    
    /**
     * Lấy danh sách tất cả comments (admin view)
     */
    Page<AdminCommentResponse> getAllCommentsByAdmin(int page, int size);
    
    /**
     * Lấy comment theo ID (admin view)
     */
    AdminCommentResponse getCommentByIdByAdmin(String commentId);
    
    /**
     * Xóa comment (admin)
     */
    void deleteCommentByAdmin(String commentId);
    
    /**
     * Khôi phục comment đã xóa (admin)
     */
    void restoreCommentByAdmin(String commentId);
    
    /**
     * Lấy comments theo post (admin view)
     */
    Page<AdminCommentResponse> getCommentsByPostByAdmin(String postId, int page, int size);
    
    /**
     * Lấy thống kê comments
     */
    Map<String, Object> getCommentStatistics();
    
    // ========== TAG MANAGEMENT ==========
    
    /**
     * Lấy danh sách tất cả tags (admin view)
     */
    Page<AdminTagResponse> getAllTagsByAdmin(int page, int size);
    
    /**
     * Lấy tag theo ID (admin view)
     */
    AdminTagResponse getTagByIdByAdmin(String tagId);
    
    /**
     * Tạo tag mới (admin)
     */
    AdminTagResponse createTagByAdmin(String name, String description);
    
    /**
     * Cập nhật tag (admin)
     */
    AdminTagResponse updateTagByAdmin(String tagId, String name, String description);
    
    /**
     * Xóa tag (admin)
     */
    void deleteTagByAdmin(String tagId);
    
    /**
     * Lấy thống kê tags
     */
    Map<String, Object> getTagStatistics();
    
    // ========== ADVANCED STATISTICS ==========
    
    /**
     * Lấy thống kê tổng quan hệ thống
     */
    Map<String, Object> getSystemStatistics();
    
    /**
     * Lấy thống kê users theo thời gian
     */
    StatisticsResponse getUserStatistics();
    
    /**
     * Lấy thống kê posts theo thời gian
     */
    StatisticsResponse getPostStatistics();
    
    /**
     * Lấy thống kê restaurants
     */
    StatisticsResponse getRestaurantStatistics();
    
    /**
     * Lấy thống kê doanh thu
     */
    StatisticsResponse getRevenueStatistics();
    
    /**
     * Lấy thống kê tương tác
     */
    StatisticsResponse getEngagementStatistics();
    
    /**
     * Lấy thống kê challenges
     */
    StatisticsResponse getChallengeStatistics();
}
