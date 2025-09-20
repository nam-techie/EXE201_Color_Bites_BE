package com.exe201.color_bites_be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

/**
 * Response DTO cho tổng hợp thông tin reaction của bài viết
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReactionSummaryResponse {
    
    // ID bài viết
    private String postId;
    
    // Tổng số reaction
    private Long totalReactions;
    
    // Loại reaction chính (hiện tại chỉ có LOVE)
    private String reactionType;
    
    // User hiện tại đã react chưa
    private Boolean hasUserReacted;

    private List<RecentReactor> recentReactors;
    
    /**
     * Inner class cho thông tin người react gần đây
     */
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RecentReactor {
        
        // ID người react
        private String accountId;
        
        // Tên người react
        private String authorName;
        
        // Avatar người react
        private String authorAvatar;
    }
}
