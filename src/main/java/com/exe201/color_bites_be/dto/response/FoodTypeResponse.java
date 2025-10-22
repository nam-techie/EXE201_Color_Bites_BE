package com.exe201.color_bites_be.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FoodTypeResponse {
    
    private String id;
    private String name;
    private LocalDateTime createdAt;
    private Long usageCount; // Number of restaurants using this food type
}
