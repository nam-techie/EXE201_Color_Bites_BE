package com.exe201.color_bites_be.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for bulk operations
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkOperationRequest {
    
    private List<String> ids;              // List of IDs to operate on
    private String operation;              // Operation type: activate, deactivate, delete
}
