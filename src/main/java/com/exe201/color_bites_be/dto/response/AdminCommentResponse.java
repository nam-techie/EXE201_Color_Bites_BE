package com.exe201.color_bites_be.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO response cho admin view comments
 * Chứa thông tin chi tiết về comment cho admin quản lý
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminCommentResponse {
    
    private String id;
    private String postId;
    private String postTitle;
    private String content;
    private String accountId;
    private String accountName;
    private String parentCommentId;
    private Integer replyCount;
    private Boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Thông tin bổ sung cho admin
    private String authorEmail;
    private Boolean authorIsActive;
    private String authorRole;
    private String postAuthorName;
    private String postAuthorEmail;
}
