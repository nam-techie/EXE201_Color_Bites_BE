package com.exe201.color_bites_be.dto.response;

import com.exe201.color_bites_be.enums.ChallengeType;
import com.exe201.color_bites_be.dto.request.TypeObjectRequest;
import com.exe201.color_bites_be.dto.request.ImageObjectRequest;
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
    private String typeObjId;
    private List<ImageObjectRequest> images; // JSON array embedded
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
    private Integer participantCount; // Number of participants
}

