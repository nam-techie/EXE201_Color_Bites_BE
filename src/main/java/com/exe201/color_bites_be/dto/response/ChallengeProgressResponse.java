package com.exe201.color_bites_be.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChallengeProgressResponse {
    
    private String participationId;
    private String challengeId;
    private String challengeTitle;
    private Integer progressCount;
    private Integer targetCount;
    private Double progressPercentage;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime completedAt;
    private String status;
    
    // Additional progress details
    private Long totalEntries;
    private Long approvedEntries;
    private Long pendingEntries;
    private Long rejectedEntries;
}






