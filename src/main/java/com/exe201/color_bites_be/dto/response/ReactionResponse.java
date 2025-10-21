package com.exe201.color_bites_be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

/**
 * Response DTO cho thông tin reaction
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReactionResponse {
    
    // ID của reaction
    private String id;
    
    // ID bài viết được react
    private String postId;
    
    // ID người react
    private String accountId;
    
    // Tên người react
    private String authorName;
    
    // Avatar người react
    private String authorAvatar;
    
    // Loại reaction (hiện tại chỉ có LOVE)
    private String reactionType;
    
    // Thời gian tạo reaction
    private LocalDateTime createdAt;
}
