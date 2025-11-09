package com.exe201.color_bites_be.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO response cho post statistics endpoint
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostStatisticsResponse {
    private Long totalPosts;
    private LocalDateTime lastUpdated;
    private String systemStatus;
}

