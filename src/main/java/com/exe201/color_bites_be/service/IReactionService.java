package com.exe201.color_bites_be.service;

import com.exe201.color_bites_be.dto.response.ReactionResponse;
import com.exe201.color_bites_be.dto.response.ReactionSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * Service interface cho quản lý reaction system
 * Thiết kế đơn giản như Instagram - chỉ hỗ trợ LOVE reaction
 */
public interface IReactionService {
    
    /**
     * Toggle reaction cho bài viết (like/unlike)
     * Tự động detect trạng thái hiện tại và toggle
     * 
     * @param postId ID của bài viết
     * @return true nếu đã like, false nếu đã unlike
     */
    boolean toggleReaction(String postId);
    
    /**
     * Kiểm tra user hiện tại đã react bài viết chưa
     * 
     * @param postId ID của bài viết
     * @param accountId ID của user (null = user hiện tại)
     * @return true nếu đã react
     */
    boolean hasUserReacted(String postId, String accountId);
    
    /**
     * Lấy tổng số reaction của bài viết
     * 
     * @param postId ID của bài viết
     * @return số lượng reaction
     */
    long getReactionCount(String postId);
    
    /**
     * Lấy danh sách người đã react bài viết (phân trang)
     * 
     * @param postId ID của bài viết
     * @param pageable thông tin phân trang
     * @return danh sách reaction với thông tin người react
     */
    Page<ReactionResponse> getReactionsByPost(String postId, Pageable pageable);
    
    /**
     * Lấy tổng hợp thông tin reaction của bài viết
     * Bao gồm: tổng số, trạng thái user, người react gần đây
     * 
     * @param postId ID của bài viết
     * @return thông tin tổng hợp reaction
     */
    ReactionSummaryResponse getReactionSummary(String postId);
    
    /**
     * Lấy danh sách bài viết user đã like (phân trang)
     * 
     * @param accountId ID của user (null = user hiện tại)
     * @param pageable thông tin phân trang
     * @return danh sách ID bài viết đã like
     */
    Page<String> getPostsLikedByUser(String accountId, Pageable pageable);
    
    /**
     * Xóa tất cả reaction của bài viết
     * Sử dụng khi xóa bài viết
     * 
     * @param postId ID của bài viết
     */
    void deleteAllReactionsByPost(String postId);
    
    /**
     * Cập nhật reaction count trong bài viết
     * Method helper cho PostService
     * 
     * @param postId ID của bài viết
     * @return reaction count mới
     */
    long updatePostReactionCount(String postId);
}
