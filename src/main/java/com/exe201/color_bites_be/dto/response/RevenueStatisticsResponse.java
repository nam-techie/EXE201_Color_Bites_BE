package com.exe201.color_bites_be.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO response cho revenue statistics endpoint
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RevenueStatisticsResponse {
    private Long totalTransactions;
    private Double totalRevenue;
    private Double monthlyRevenue;
    private Double dailyRevenue;
    private Long successfulTransactions;
    private Long failedTransactions;
    private Long pendingTransactions;
    private LocalDateTime lastUpdated;
    private String systemStatus;
}

