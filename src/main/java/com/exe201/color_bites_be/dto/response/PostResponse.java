package com.exe201.color_bites_be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostResponse {
    private String id;
    private String accountId;
    private String authorName; // Tên tác giả từ UserInformation
    private String authorAvatar; // Avatar tác giả từ UserInformation
    private String title;
    private String content;
    private String mood;
    private List<String> imageUrls;
    private String videoUrl;
    private Integer reactionCount;
    private Integer commentCount;
    private List<TagResponse> tags;
    private Boolean isOwner; // Người xem có phải chủ bài viết không
    private Boolean hasReacted; // Người xem đã react chưa
    private String userReactionType; // Loại reaction của người xem
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
