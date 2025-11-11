package com.exe201.color_bites_be.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO response cho engagement statistics endpoint
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EngagementStatisticsResponse {
    private Long totalComments;
    private Long totalReactions;
    private Long totalFavorites;
    private Double averageRating;
    private Long totalMoodMaps;
    private Long totalQuizzes;
    private LocalDateTime lastUpdated;
    private String systemStatus;
}

