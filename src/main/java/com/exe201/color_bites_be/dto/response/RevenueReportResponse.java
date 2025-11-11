package com.exe201.color_bites_be.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO response cho báo cáo tổng doanh thu
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RevenueReportResponse {
    private Double totalRevenue;
    private Double monthlyRevenue;
    private Double dailyRevenue;
    private Long totalTransactions;
    private Long successfulTransactions;
    private Long failedTransactions;
    private Long pendingTransactions;
    private List<DailyRevenue> dailyRevenues;
    private List<MonthlyRevenue> monthlyRevenues;
    private LocalDateTime reportGeneratedAt;
    
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DailyRevenue {
        private String date;
        private Double revenue;
        private Long transactionCount;
    }
    
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MonthlyRevenue {
        private String month;
        private Double revenue;
        private Long transactionCount;
    }
}

