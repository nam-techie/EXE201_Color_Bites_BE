package com.exe201.color_bites_be.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO response cho user statistics endpoint
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserStatisticsResponse {
    private Long totalUsers;
    private Long activeUsers;
    private LocalDateTime lastUpdated;
    private String systemStatus;
}

