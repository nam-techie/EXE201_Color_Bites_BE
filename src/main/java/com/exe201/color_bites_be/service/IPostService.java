package com.exe201.color_bites_be.service;

import com.exe201.color_bites_be.dto.request.CreatePostRequest;
import com.exe201.color_bites_be.dto.request.UpdatePostRequest;
import com.exe201.color_bites_be.dto.response.PostResponse;
import org.springframework.data.domain.Page;

/**
 * Interface định nghĩa các phương thức quản lý bài viết
 * Bao gồm CRUD, tìm kiếm, phân trang và tương tác với bài viết
 */
public interface IPostService {
    
    /**
     * Tạo bài viết mới
     * @param accountId ID của tài khoản tạo bài viết
     * @param request Thông tin bài viết
     * @return PostResponse Bài viết đã tạo
     */
    PostResponse createPost(String accountId, CreatePostRequest request);
    
    /**
     * Lấy bài viết theo ID
     * @param postId ID của bài viết
     * @param currentAccountId ID của tài khoản hiện tại (để check reaction)
     * @return PostResponse Thông tin bài viết
     */
    PostResponse readPostById(String postId, String currentAccountId);
    
    /**
     * Lấy tất cả bài viết với phân trang
     * @param page Số trang
     * @param size Kích thước trang
     * @param currentAccountId ID của tài khoản hiện tại
     * @return Page<PostResponse> Danh sách bài viết có phân trang
     */
    Page<PostResponse> readAllPosts(int page, int size, String currentAccountId);
    
    /**
     * Lấy bài viết của một người dùng
     * @param accountId ID của người dùng
     * @param page Số trang
     * @param size Kích thước trang
     * @param currentAccountId ID của tài khoản hiện tại
     * @return Page<PostResponse> Danh sách bài viết của người dùng
     */
    Page<PostResponse> readPostsByUser(String accountId, int page, int size, String currentAccountId);
    
    /**
     * Tìm kiếm bài viết theo từ khóa
     * @param keyword Từ khóa tìm kiếm
     * @param page Số trang
     * @param size Kích thước trang
     * @param currentAccountId ID của tài khoản hiện tại
     * @return Page<PostResponse> Kết quả tìm kiếm
     */
    Page<PostResponse> searchPosts(String keyword, int page, int size, String currentAccountId);
    
    /**
     * Lấy bài viết theo mood
     * @param mood Mood của bài viết
     * @param page Số trang
     * @param size Kích thước trang
     * @param currentAccountId ID của tài khoản hiện tại
     * @return Page<PostResponse> Danh sách bài viết theo mood
     */
    Page<PostResponse> readPostsByMood(String mood, int page, int size, String currentAccountId);
    
    /**
     * Cập nhật bài viết
     * @param postId ID của bài viết
     * @param accountId ID của tài khoản (để check quyền)
     * @param request Thông tin cập nhật
     * @return PostResponse Bài viết đã cập nhật
     */
    PostResponse editPost(String postId, String accountId, UpdatePostRequest request);
    
    /**
     * Xóa bài viết (soft delete)
     * @param postId ID của bài viết
     * @param accountId ID của tài khoản (để check quyền)
     */
    void deletePost(String postId, String accountId);
    
    /**
     * Đếm số lượng bài viết của người dùng
     * @param accountId ID của người dùng
     * @return long Số lượng bài viết
     */
    long countPostsByUser(String accountId);
    
    /**
     * React/Unreact bài viết
     * @param postId ID của bài viết
     * @param accountId ID của tài khoản
     * @param reactionType Loại reaction (like, love, dislike...)
     */
    void toggleReaction(String postId, String accountId, String reactionType);
}
