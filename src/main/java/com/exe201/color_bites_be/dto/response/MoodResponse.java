package com.exe201.color_bites_be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

/**
 * Response DTO cho mood
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MoodResponse {
    
    private String id;
    
    private String name;
    
    // Unicode emoji
    private String emoji;
    
    private LocalDateTime createdAt;
    
    // Thống kê số lượng bài viết sử dụng mood này
    private Long postCount;
}
