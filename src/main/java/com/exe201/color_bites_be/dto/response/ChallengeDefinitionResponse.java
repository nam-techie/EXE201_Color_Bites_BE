package com.exe201.color_bites_be.dto.response;

import com.exe201.color_bites_be.enums.ChallengeType;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChallengeDefinitionResponse {
    
    private String id;
    private String title;
    private String description;
    private ChallengeType challengeType;
    private String restaurantId;
    private String foodTypeId;
    private Integer targetCount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String rewardDescription;
    private String createdBy;
    private LocalDateTime createdAt;
    private Boolean isActive;
    
    // Additional fields for display
    private String restaurantName; // If restaurantId is provided
    private String foodTypeName; // If foodTypeId is provided
    private Long participantCount; // Number of participants
}
