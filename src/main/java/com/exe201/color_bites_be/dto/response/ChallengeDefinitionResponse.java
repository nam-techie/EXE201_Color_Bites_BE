package com.exe201.color_bites_be.dto.response;

import com.exe201.color_bites_be.enums.ChallengeType;
import com.exe201.color_bites_be.model.TypeObject;
import com.exe201.color_bites_be.model.ImageObject;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

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
    private TypeObject typeObj; // Changed from foodTypeId to typeObj (JSON embedded)
    private List<ImageObject> images; // JSON array embedded
    private Integer targetCount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String rewardDescription;
    private String createdBy;
    private LocalDateTime createdAt;
    private Boolean isActive;
    
    // Additional fields for display
    private String restaurantName; // If restaurantId is provided
    private String typeObjName; // If typeObj is provided (replaces foodTypeName)
    private Long participantCount; // Number of participants
}

