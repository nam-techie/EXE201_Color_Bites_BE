package com.exe201.color_bites_be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {
    private String id;
    private String postId;
    private String accountId;
    private String parentCommentId;
    private Integer depth;
    private String content;
    private Boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Thông tin user
    private String username;
    private String userAvatar;
    
    // Danh sách reply
    private List<CommentResponse> replies;
    
    // Số lượng reply
    private Integer replyCount;
} 