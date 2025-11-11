package com.exe201.color_bites_be.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO response cho admin view tags
 * Chứa thông tin chi tiết về tag cho admin quản lý
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminTagResponse {
    
    private String id;
    private String name;
    private String description;
    private Long usageCount;
    private Boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Thông tin bổ sung cho admin
    private String createdBy;
    private String createdByName;
    private String createdByEmail;
    private Long postCount;
    private Long restaurantCount;
}
