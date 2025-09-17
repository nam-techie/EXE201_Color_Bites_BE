package com.exe201.color_bites_be.service;

import com.exe201.color_bites_be.dto.request.CreateCommentRequest;
import com.exe201.color_bites_be.dto.request.UpdateCommentRequest;
import com.exe201.color_bites_be.dto.response.CommentResponse;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Interface định nghĩa các phương thức quản lý comment
 * Hỗ trợ nested comments và các tương tác với comment
 */
public interface ICommentService {
    
    /**
     * Tạo comment mới
     * @param postId ID của bài viết
     * @param accountId ID của tài khoản
     * @param request Thông tin comment
     * @return CommentResponse Comment đã tạo
     */
    CommentResponse createComment(String postId, String accountId, CreateCommentRequest request);
    
    /**
     * Lấy comment theo ID
     * @param commentId ID của comment
     * @param currentAccountId ID của tài khoản hiện tại
     * @return CommentResponse Thông tin comment
     */
    CommentResponse readCommentById(String commentId, String currentAccountId);
    
    /**
     * Lấy comment gốc của bài viết (không có parent)
     * @param postId ID của bài viết
     * @param page Số trang
     * @param size Kích thước trang
     * @param currentAccountId ID của tài khoản hiện tại
     * @return Page<CommentResponse> Danh sách comment gốc
     */
    Page<CommentResponse> readRootCommentsByPost(String postId, int page, int size, String currentAccountId);
    
    /**
     * Lấy tất cả comment của bài viết
     * @param postId ID của bài viết
     * @param page Số trang
     * @param size Kích thước trang
     * @param currentAccountId ID của tài khoản hiện tại
     * @return Page<CommentResponse> Tất cả comment
     */
    Page<CommentResponse> readAllCommentsByPost(String postId, int page, int size, String currentAccountId);
    
    /**
     * Lấy replies của một comment
     * @param parentCommentId ID của comment cha
     * @param currentAccountId ID của tài khoản hiện tại
     * @return List<CommentResponse> Danh sách reply
     */
    List<CommentResponse> readRepliesByComment(String parentCommentId, String currentAccountId);
    
    /**
     * Cập nhật comment
     * @param commentId ID của comment
     * @param accountId ID của tài khoản (để check quyền)
     * @param request Thông tin cập nhật
     * @return CommentResponse Comment đã cập nhật
     */
    CommentResponse editComment(String commentId, String accountId, UpdateCommentRequest request);
    
    /**
     * Xóa comment (soft delete)
     * @param commentId ID của comment
     * @param accountId ID của tài khoản (để check quyền)
     */
    void deleteComment(String commentId, String accountId);
    
    /**
     * Đếm tổng số comment của bài viết
     * @param postId ID của bài viết
     * @return long Số lượng comment
     */
    long countCommentsByPost(String postId);
    
    /**
     * Đếm số comment gốc của bài viết
     * @param postId ID của bài viết
     * @return long Số lượng comment gốc
     */
    long countRootCommentsByPost(String postId);
    
    /**
     * Lấy comment của user trong bài viết
     * @param postId ID của bài viết
     * @param accountId ID của user
     * @param currentAccountId ID của tài khoản hiện tại
     * @return List<CommentResponse> Comment của user
     */
    List<CommentResponse> readCommentsByUser(String postId, String accountId, String currentAccountId);
    
    /**
     * Xóa tất cả comment của bài viết (khi bài viết bị xóa)
     * @param postId ID của bài viết
     */
    void deleteAllCommentsByPost(String postId);
}
