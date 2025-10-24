package com.exe201.color_bites_be.repository;

import com.exe201.color_bites_be.entity.TypeObjects;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for TypeObjects entity
 * Manages global catalog of food types
 */
@Repository
public interface TypeObjectsRepository extends MongoRepository<TypeObjects, String> {

    /**
     * Find by name (case-insensitive)
     */
    @Query("{'name': {$regex: ?0, $options: 'i'}}")
    List<TypeObjects> findByNameContainingIgnoreCase(String name);

    /**
     * Find by exact name
     */
    Optional<TypeObjects> findByName(String name);

    /**
     * Find all active type objects
     */
    List<TypeObjects> findByIsActiveTrue();

    /**
     * Find all inactive type objects
     */
    List<TypeObjects> findByIsActiveFalse();

    /**
     * Check if name exists (case-insensitive)
     */
    @Query("{'name': {$regex: ?0, $options: 'i'}}")
    boolean existsByNameIgnoreCase(String name);

    // ============================================
    // ADVANCED FILTERING & SEARCH METHODS
    // ============================================

    /**
     * Advanced search with multiple criteria
     */
    @Query("{'$and': [" +
           "{'name': {$regex: ?0, $options: 'i'}}, " +
           "{'is_active': ?1}, " +
           "{'created_at': {$gte: ?2, $lte: ?3}}" +
           "]}")
    Page<TypeObjects> findByAdvancedCriteria(String name, Boolean isActive, 
                                           LocalDateTime startDate, LocalDateTime endDate, 
                                           Pageable pageable);

    /**
     * Find by status with pagination
     */
    Page<TypeObjects> findByIsActive(Boolean isActive, Pageable pageable);

    /**
     * Find by date range
     */
    @Query("{'created_at': {$gte: ?0, $lte: ?1}}")
    Page<TypeObjects> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * Find by name pattern with pagination
     */
    @Query("{'name': {$regex: ?0, $options: 'i'}}")
    Page<TypeObjects> findByNameContainingIgnoreCase(String name, Pageable pageable);

    /**
     * Find by status and name pattern
     */
    @Query("{'$and': [" +
           "{'name': {$regex: ?0, $options: 'i'}}, " +
           "{'is_active': ?1}" +
           "]}")
    Page<TypeObjects> findByNameContainingAndIsActive(String name, Boolean isActive, Pageable pageable);

    /**
     * Find by multiple IDs (for bulk operations)
     */
    List<TypeObjects> findByIdIn(List<String> ids);

    /**
     * Count by status
     */
    long countByIsActive(Boolean isActive);

    /**
     * Find recently created (last N days)
     */
    @Query("{'created_at': {$gte: ?0}}")
    List<TypeObjects> findByCreatedAtAfter(LocalDateTime date);

    /**
     * Find recently updated (last N days)
     */
    @Query("{'updated_at': {$gte: ?0}}")
    List<TypeObjects> findByUpdatedAtAfter(LocalDateTime date);
}
