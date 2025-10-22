package com.exe201.color_bites_be.dto.response;

import com.exe201.color_bites_be.enums.ParticipationStatus;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChallengeParticipationResponse {
    
    private String id;
    private String accountId;
    private String challengeId;
    private ParticipationStatus status;
    private Integer progressCount;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;
    
    // Additional fields for display
    private String challengeTitle;
    private Integer targetCount;
    private String challengeType;
    private Long totalEntries; // Total entries submitted
    private Long approvedEntries; // Approved entries count
}
