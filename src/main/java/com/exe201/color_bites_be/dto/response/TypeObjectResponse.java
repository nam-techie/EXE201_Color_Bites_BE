package com.exe201.color_bites_be.dto.response;

import lombok.*;
import java.time.LocalDateTime;

/**
 * Response DTO for TypeObject
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TypeObjectResponse {
    
    private String id;
    private String name;
    private String imageUrl;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
