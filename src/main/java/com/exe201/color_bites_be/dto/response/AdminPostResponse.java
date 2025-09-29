package com.exe201.color_bites_be.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO response cho admin view posts
 * Chứa thông tin chi tiết về bài viết cho admin quản lý
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminPostResponse {
    
    private String id;
    private String accountId;
    private String accountName;
    private String content;
    private String moodId;
    private String moodName;
    private Integer reactionCount;
    private Integer commentCount;
    private Boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Thông tin bổ sung cho admin
    private String authorEmail;
    private Boolean authorIsActive;
    private String authorRole;
}
