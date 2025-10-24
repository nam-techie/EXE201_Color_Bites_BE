package com.exe201.color_bites_be.service.impl;

import com.exe201.color_bites_be.dto.request.AdvancedSearchRequest;
import com.exe201.color_bites_be.dto.request.BulkOperationRequest;
import com.exe201.color_bites_be.dto.request.CreateTypeObjectRequest;
import com.exe201.color_bites_be.dto.request.UpdateTypeObjectRequest;
import com.exe201.color_bites_be.dto.response.TypeObjectResponse;
import com.exe201.color_bites_be.entity.TypeObjects;

import com.exe201.color_bites_be.exception.ResourceNotFoundException;
import com.exe201.color_bites_be.repository.TypeObjectsRepository;
import com.exe201.color_bites_be.service.ITypeObjectsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service implementation for TypeObjects management
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TypeObjectsServiceImpl implements ITypeObjectsService {

    private final TypeObjectsRepository typeObjectsRepository;

    @Override
    public TypeObjectResponse createTypeObject(CreateTypeObjectRequest request) {
        log.info("Creating new TypeObject with name: {}", request.getName());
        
        // Check if name already exists
        if (typeObjectsRepository.existsByNameIgnoreCase(request.getName())) {
            throw new IllegalArgumentException("Tên loại món ăn đã tồn tại: " + request.getName());
        }

        TypeObjects typeObject = new TypeObjects();
        typeObject.setName(request.getName());
        typeObject.setImageUrl(request.getImageUrl());
        typeObject.setIsActive(true);
        typeObject.setCreatedAt(LocalDateTime.now());
        typeObject.setUpdatedAt(LocalDateTime.now());

        TypeObjects saved = typeObjectsRepository.save(typeObject);
        log.info("Created TypeObject with ID: {}", saved.getId());
        
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public TypeObjectResponse getTypeObjectById(String id) {
        log.info("Getting TypeObject by ID: {}", id);
        
        TypeObjects typeObject = typeObjectsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy loại món ăn với ID: " + id));
        
        return mapToResponse(typeObject);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TypeObjectResponse> getAllTypeObjects(Pageable pageable) {
        log.info("Getting all TypeObjects with pagination");
        
        Page<TypeObjects> typeObjects = typeObjectsRepository.findAll(pageable);
        return typeObjects.map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TypeObjectResponse> getActiveTypeObjects() {
        log.info("Getting all active TypeObjects");
        
        List<TypeObjects> typeObjects = typeObjectsRepository.findByIsActiveTrue();
        return typeObjects.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TypeObjectResponse> searchTypeObjectsByName(String name) {
        log.info("Searching TypeObjects by name: {}", name);
        
        List<TypeObjects> typeObjects = typeObjectsRepository.findByNameContainingIgnoreCase(name);
        return typeObjects.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TypeObjectResponse updateTypeObject(String id, UpdateTypeObjectRequest request) {
        log.info("Updating TypeObject with ID: {}", id);
        
        TypeObjects typeObject = typeObjectsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy loại món ăn với ID: " + id));

        // Check if new name conflicts with existing names (excluding current)
        if (!typeObject.getName().equalsIgnoreCase(request.getName()) && 
            typeObjectsRepository.existsByNameIgnoreCase(request.getName())) {
            throw new IllegalArgumentException("Tên loại món ăn đã tồn tại: " + request.getName());
        }

        typeObject.setName(request.getName());
        typeObject.setImageUrl(request.getImageUrl());
        typeObject.setIsActive(request.getIsActive());
        typeObject.setUpdatedAt(LocalDateTime.now());

        TypeObjects saved = typeObjectsRepository.save(typeObject);
        log.info("Updated TypeObject with ID: {}", saved.getId());
        
        return mapToResponse(saved);
    }

    @Override
    public void deleteTypeObject(String id) {
        log.info("Deleting TypeObject with ID: {}", id);
        
        TypeObjects typeObject = typeObjectsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy loại món ăn với ID: " + id));

        typeObject.setIsActive(false);
        typeObject.setUpdatedAt(LocalDateTime.now());
        typeObjectsRepository.save(typeObject);
        
        log.info("Soft deleted TypeObject with ID: {}", id);
    }

    @Override
    public TypeObjectResponse activateTypeObject(String id) {
        log.info("Activating TypeObject with ID: {}", id);
        
        TypeObjects typeObject = typeObjectsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy loại món ăn với ID: " + id));

        typeObject.setIsActive(true);
        typeObject.setUpdatedAt(LocalDateTime.now());

        TypeObjects saved = typeObjectsRepository.save(typeObject);
        log.info("Activated TypeObject with ID: {}", saved.getId());
        
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return typeObjectsRepository.existsByNameIgnoreCase(name);
    }

    @Override
    @Transactional(readOnly = true)
    public TypeObjectResponse getTypeObjectByName(String name) {
        log.info("Getting TypeObject by name: {}", name);
        
        TypeObjects typeObject = typeObjectsRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy loại món ăn với tên: " + name));
        
        return mapToResponse(typeObject);
    }

    /**
     * Map TypeObjects entity to TypeObjectResponse DTO
     */
    private TypeObjectResponse mapToResponse(TypeObjects typeObject) {
        TypeObjectResponse response = new TypeObjectResponse();
        response.setId(typeObject.getId());
        response.setName(typeObject.getName());
        response.setImageUrl(typeObject.getImageUrl());
        response.setIsActive(typeObject.getIsActive());
        response.setCreatedAt(typeObject.getCreatedAt());
        response.setUpdatedAt(typeObject.getUpdatedAt());
        return response;
    }

    // ============================================
    // ADVANCED SEARCH & FILTERING METHODS
    // ============================================

    @Override
    @Transactional(readOnly = true)
    public Page<TypeObjectResponse> advancedSearch(AdvancedSearchRequest request) {
        log.info("Advanced search with criteria: {}", request);
        
        // Build pageable with sorting
        Pageable pageable = buildPageable(request);
        
        // Use advanced criteria search
        Page<TypeObjects> typeObjects = typeObjectsRepository.findByAdvancedCriteria(
            request.getName() != null ? request.getName() : "",
            request.getIsActive(),
            request.getStartDate() != null ? request.getStartDate() : LocalDateTime.of(1900, 1, 1, 0, 0),
            request.getEndDate() != null ? request.getEndDate() : LocalDateTime.now(),
            pageable
        );
        
        return typeObjects.map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TypeObjectResponse> getTypeObjectsByStatus(Boolean isActive, Pageable pageable) {
        log.info("Getting TypeObjects by status: {}", isActive);
        
        Page<TypeObjects> typeObjects = typeObjectsRepository.findByIsActive(isActive, pageable);
        return typeObjects.map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TypeObjectResponse> getTypeObjectsByDateRange(LocalDateTime startDate, 
                                                             LocalDateTime endDate, 
                                                             Pageable pageable) {
        log.info("Getting TypeObjects by date range: {} to {}", startDate, endDate);
        
        Page<TypeObjects> typeObjects = typeObjectsRepository.findByCreatedAtBetween(startDate, endDate, pageable);
        return typeObjects.map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TypeObjectResponse> searchTypeObjectsByName(String name, Pageable pageable) {
        log.info("Searching TypeObjects by name: {} with pagination", name);
        
        Page<TypeObjects> typeObjects = typeObjectsRepository.findByNameContainingIgnoreCase(name, pageable);
        return typeObjects.map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TypeObjectResponse> getTypeObjectsByStatusAndName(Boolean isActive, String name, Pageable pageable) {
        log.info("Getting TypeObjects by status: {} and name: {}", isActive, name);
        
        Page<TypeObjects> typeObjects = typeObjectsRepository.findByNameContainingAndIsActive(name, isActive, pageable);
        return typeObjects.map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TypeObjectResponse> getRecentlyCreated(int days) {
        log.info("Getting recently created TypeObjects for last {} days", days);
        
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
        List<TypeObjects> typeObjects = typeObjectsRepository.findByCreatedAtAfter(cutoffDate);
        
        return typeObjects.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TypeObjectResponse> getRecentlyUpdated(int days) {
        log.info("Getting recently updated TypeObjects for last {} days", days);
        
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
        List<TypeObjects> typeObjects = typeObjectsRepository.findByUpdatedAtAfter(cutoffDate);
        
        return typeObjects.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getStatistics() {
        log.info("Getting TypeObjects statistics");
        
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", typeObjectsRepository.count());
        stats.put("active", typeObjectsRepository.countByIsActive(true));
        stats.put("inactive", typeObjectsRepository.countByIsActive(false));
        
        return stats;
    }

    // ============================================
    // BULK OPERATIONS
    // ============================================

    @Override
    public List<TypeObjectResponse> bulkActivate(List<String> ids) {
        log.info("Bulk activating {} TypeObjects", ids.size());
        
        List<TypeObjects> typeObjects = typeObjectsRepository.findByIdIn(ids);
        
        for (TypeObjects typeObject : typeObjects) {
            typeObject.setIsActive(true);
            typeObject.setUpdatedAt(LocalDateTime.now());
        }
        
        List<TypeObjects> saved = typeObjectsRepository.saveAll(typeObjects);
        log.info("Bulk activated {} TypeObjects", saved.size());
        
        return saved.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TypeObjectResponse> bulkDeactivate(List<String> ids) {
        log.info("Bulk deactivating {} TypeObjects", ids.size());
        
        List<TypeObjects> typeObjects = typeObjectsRepository.findByIdIn(ids);
        
        for (TypeObjects typeObject : typeObjects) {
            typeObject.setIsActive(false);
            typeObject.setUpdatedAt(LocalDateTime.now());
        }
        
        List<TypeObjects> saved = typeObjectsRepository.saveAll(typeObjects);
        log.info("Bulk deactivated {} TypeObjects", saved.size());
        
        return saved.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void bulkDelete(List<String> ids) {
        log.info("Bulk deleting {} TypeObjects", ids.size());
        
        List<TypeObjects> typeObjects = typeObjectsRepository.findByIdIn(ids);
        
        for (TypeObjects typeObject : typeObjects) {
            typeObject.setIsActive(false);
            typeObject.setUpdatedAt(LocalDateTime.now());
        }
        
        typeObjectsRepository.saveAll(typeObjects);
        log.info("Bulk deleted {} TypeObjects", typeObjects.size());
    }

    @Override
    public List<TypeObjectResponse> executeBulkOperation(BulkOperationRequest request) {
        log.info("Executing bulk operation: {} on {} items", request.getOperation(), request.getIds().size());
        
        switch (request.getOperation().toLowerCase()) {
            case "activate":
                return bulkActivate(request.getIds());
            case "deactivate":
                return bulkDeactivate(request.getIds());
            case "delete":
                bulkDelete(request.getIds());
                return List.of();
            default:
                throw new IllegalArgumentException("Invalid bulk operation: " + request.getOperation());
        }
    }

    /**
     * Build Pageable with sorting
     */
    private Pageable buildPageable(AdvancedSearchRequest request) {
        Sort sort = Sort.by(Sort.Direction.ASC, "name"); // Default sort
        
        if (request.getSortBy() != null && !request.getSortBy().isEmpty()) {
            Sort.Direction direction = Sort.Direction.ASC;
            if (request.getSortDirection() != null && 
                request.getSortDirection().equalsIgnoreCase("desc")) {
                direction = Sort.Direction.DESC;
            }
            sort = Sort.by(direction, request.getSortBy());
        }
        
        int page = request.getPage() != null ? request.getPage() : 0;
        int size = request.getSize() != null ? request.getSize() : 20;
        
        return PageRequest.of(page, size, sort);
    }
}
