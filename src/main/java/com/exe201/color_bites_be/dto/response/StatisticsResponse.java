package com.exe201.color_bites_be.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO response cho statistics endpoints
 * Chứa thông tin thống kê chi tiết cho admin dashboard
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StatisticsResponse {
    
    // Basic counts
    private Long totalUsers;
    private Long activeUsers;
    private Long totalPosts;
    private Long totalRestaurants;
    private Long totalComments;
    private Long totalTags;
    private Long totalChallenges;
    private Long totalTransactions;
    
    // Revenue statistics
    private Double totalRevenue;
    private Double monthlyRevenue;
    private Double dailyRevenue;
    private Long successfulTransactions;
    private Long failedTransactions;
    private Long pendingTransactions;
    
    // Engagement statistics
    private Long totalReactions;
    private Long totalFavorites;
    private Double averageRating;
    private Long totalMoodMaps;
    private Long totalQuizzes;
    
    // System health
    private LocalDateTime lastUpdated;
    private String systemStatus;
}
