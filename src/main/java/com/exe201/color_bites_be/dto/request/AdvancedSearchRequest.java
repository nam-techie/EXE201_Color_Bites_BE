package com.exe201.color_bites_be.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for advanced search criteria
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdvancedSearchRequest {
    
    private String name;                    // Search by name pattern
    private Boolean isActive;              // Filter by status
    private LocalDateTime startDate;       // Date range start
    private LocalDateTime endDate;         // Date range end
    private String sortBy;                 // Sort field (name, created_at, updated_at)
    private String sortDirection;          // Sort direction (asc, desc)
    private Integer page;                  // Page number (0-based)
    private Integer size;                  // Page size
}
