package com.exe201.color_bites_be.dto.response;

import com.exe201.color_bites_be.enums.Visibility;
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
    private AuthorResponsePost author;
    private String content;
    private String moodId;
    private String moodName; // Tên mood
    private String moodEmoji; // Emoji mood
    private List<String> imageUrls;
    private Integer reactionCount;
    private Integer commentCount;
    private List<TagResponse> tags;
    private Boolean isOwner; // Người xem có phải chủ bài viết không
    private Visibility visibility;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
