package com.exe201.color_bites_be.service;

import com.exe201.color_bites_be.dto.request.AdvancedSearchRequest;
import com.exe201.color_bites_be.dto.request.BulkOperationRequest;
import com.exe201.color_bites_be.dto.request.CreateTypeObjectRequest;
import com.exe201.color_bites_be.dto.request.UpdateTypeObjectRequest;
import com.exe201.color_bites_be.dto.response.TypeObjectResponse;
import com.exe201.color_bites_be.entity.TypeObjects;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for TypeObjects management
 * Handles CRUD operations for global food type catalog
 */
public interface ITypeObjectsService {

    /**
     * Create new TypeObject
     */
    TypeObjectResponse createTypeObject(CreateTypeObjectRequest request);

    /**
     * Get TypeObject by ID
     */
    TypeObjectResponse getTypeObjectById(String id);

    /**
     * Get all TypeObjects with pagination
     */
    Page<TypeObjectResponse> getAllTypeObjects(Pageable pageable);

    /**
     * Get all active TypeObjects
     */
    List<TypeObjectResponse> getActiveTypeObjects();

    /**
     * Search TypeObjects by name
     */
    List<TypeObjectResponse> searchTypeObjectsByName(String name);

    /**
     * Update TypeObject
     */
    TypeObjectResponse updateTypeObject(String id, UpdateTypeObjectRequest request);

    /**
     * Delete TypeObject (soft delete by setting isActive = false)
     */
    void deleteTypeObject(String id);

    /**
     * Activate TypeObject
     */
    TypeObjectResponse activateTypeObject(String id);

    /**
     * Check if TypeObject name exists
     */
    boolean existsByName(String name);

    /**
     * Get TypeObject by name
     */
    TypeObjectResponse getTypeObjectByName(String name);

    // ============================================
    // ADVANCED SEARCH & FILTERING METHODS
    // ============================================

    /**
     * Advanced search with multiple criteria
     */
    Page<TypeObjectResponse> advancedSearch(AdvancedSearchRequest request);

    /**
     * Get TypeObjects by status with pagination
     */
    Page<TypeObjectResponse> getTypeObjectsByStatus(Boolean isActive, Pageable pageable);

    /**
     * Get TypeObjects by date range
     */
    Page<TypeObjectResponse> getTypeObjectsByDateRange(java.time.LocalDateTime startDate, 
                                                      java.time.LocalDateTime endDate, 
                                                      Pageable pageable);

    /**
     * Get TypeObjects by name pattern with pagination
     */
    Page<TypeObjectResponse> searchTypeObjectsByName(String name, Pageable pageable);

    /**
     * Get TypeObjects by status and name pattern
     */
    Page<TypeObjectResponse> getTypeObjectsByStatusAndName(Boolean isActive, String name, Pageable pageable);

    /**
     * Get recently created TypeObjects (last N days)
     */
    List<TypeObjectResponse> getRecentlyCreated(int days);

    /**
     * Get recently updated TypeObjects (last N days)
     */
    List<TypeObjectResponse> getRecentlyUpdated(int days);

    /**
     * Get statistics
     */
    java.util.Map<String, Long> getStatistics();

    // ============================================
    // BULK OPERATIONS
    // ============================================

    /**
     * Bulk activate TypeObjects
     */
    List<TypeObjectResponse> bulkActivate(List<String> ids);

    /**
     * Bulk deactivate TypeObjects
     */
    List<TypeObjectResponse> bulkDeactivate(List<String> ids);

    /**
     * Bulk delete TypeObjects
     */
    void bulkDelete(List<String> ids);

    /**
     * Execute bulk operation
     */
    List<TypeObjectResponse> executeBulkOperation(BulkOperationRequest request);
}
