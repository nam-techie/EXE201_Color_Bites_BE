package com.exe201.color_bites_be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostResponse {
    private String id;
    private String accountId;
    private String title;
    private String content;
    private String mood;
    private List<String> imageUrls;
    private String videoUrl;
    private Integer reactionCount;
    private Integer commentCount;
    private Boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Thông tin user
    private String username;
    private String userAvatar;
    
    // Thông tin reaction của user hiện tại
    private String currentUserReaction;
    
    // Danh sách tags
    private List<String> tags;
    
    // Thống kê reaction theo loại
    private Map<String, Long> reactionStats;
} 