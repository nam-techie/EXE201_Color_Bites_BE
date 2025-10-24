package com.exe201.color_bites_be.controller;

import com.exe201.color_bites_be.dto.request.AdvancedSearchRequest;
import com.exe201.color_bites_be.dto.request.BulkOperationRequest;
import com.exe201.color_bites_be.dto.request.CreateTypeObjectRequest;
import com.exe201.color_bites_be.dto.request.UpdateTypeObjectRequest;
import com.exe201.color_bites_be.dto.response.ResponseDto;
import com.exe201.color_bites_be.dto.response.TypeObjectResponse;
import com.exe201.color_bites_be.service.ITypeObjectsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for TypeObjects management
 * Handles CRUD operations for global food type catalog
 */
@RestController
@RequestMapping("/api/type-objects")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Type Objects Management", description = "APIs for managing global food type catalog")
public class TypeObjectsController {

    private final ITypeObjectsService typeObjectsService;

    /**
     * Create new TypeObject
     */
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDto<TypeObjectResponse>> createTypeObject(
            @Valid @RequestBody CreateTypeObjectRequest request) {
        log.info("Creating TypeObject with name: {}", request.getName());
        
        TypeObjectResponse response = typeObjectsService.createTypeObject(request);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDto.<TypeObjectResponse>builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Loại món ăn đã được tạo thành công")
                        .data(response)
                        .build());
    }

    /**
     * Get TypeObject by ID
     */
    @GetMapping("/read/{id}")
    public ResponseEntity<ResponseDto<TypeObjectResponse>> getTypeObjectById(@PathVariable String id) {
        log.info("Getting TypeObject by ID: {}", id);
        
        TypeObjectResponse response = typeObjectsService.getTypeObjectById(id);
        
        return ResponseEntity.ok(ResponseDto.<TypeObjectResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Lấy thông tin loại món ăn thành công")
                .data(response)
                .build());
    }

    /**
     * Get all TypeObjects with pagination
     */
    @GetMapping("/list")
    public ResponseEntity<ResponseDto<Page<TypeObjectResponse>>> getAllTypeObjects(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Getting all TypeObjects with pagination - page: {}, size: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<TypeObjectResponse> response = typeObjectsService.getAllTypeObjects(pageable);
        
        return ResponseEntity.ok(ResponseDto.<Page<TypeObjectResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Lấy danh sách loại món ăn thành công")
                .data(response)
                .build());
    }

    /**
     * Get all active TypeObjects
     */
    @GetMapping("/read/active")
    public ResponseEntity<ResponseDto<List<TypeObjectResponse>>> getActiveTypeObjects() {
        log.info("Getting all active TypeObjects");
        
        List<TypeObjectResponse> response = typeObjectsService.getActiveTypeObjects();
        
        return ResponseEntity.ok(ResponseDto.<List<TypeObjectResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Lấy danh sách loại món ăn đang hoạt động thành công")
                .data(response)
                .build());
    }

    /**
     * Search TypeObjects by name
     */
    @GetMapping("/search")
    public ResponseEntity<ResponseDto<List<TypeObjectResponse>>> searchTypeObjectsByName(
            @RequestParam String keyword) {
        log.info("Searching TypeObjects by keyword: {}", keyword);
        
        List<TypeObjectResponse> response = typeObjectsService.searchTypeObjectsByName(keyword);
        
        return ResponseEntity.ok(ResponseDto.<List<TypeObjectResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Tìm kiếm loại món ăn thành công")
                .data(response)
                .build());
    }

    /**
     * Update TypeObject
     */
    @PutMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDto<TypeObjectResponse>> updateTypeObject(
            @PathVariable String id,
            @Valid @RequestBody UpdateTypeObjectRequest request) {
        log.info("Updating TypeObject with ID: {}", id);
        
        TypeObjectResponse response = typeObjectsService.updateTypeObject(id, request);
        
        return ResponseEntity.ok(ResponseDto.<TypeObjectResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Loại món ăn đã được cập nhật thành công")
                .data(response)
                .build());
    }

    /**
     * Delete TypeObject (soft delete)
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDto<Void>> deleteTypeObject(@PathVariable String id) {
        log.info("Deleting TypeObject with ID: {}", id);
        
        typeObjectsService.deleteTypeObject(id);
        
        return ResponseEntity.ok(ResponseDto.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Loại món ăn đã được xóa thành công")
                .build());
    }

    /**
     * Activate TypeObject
     */
    @PutMapping("/activate/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDto<TypeObjectResponse>> activateTypeObject(@PathVariable String id) {
        log.info("Activating TypeObject with ID: {}", id);
        
        TypeObjectResponse response = typeObjectsService.activateTypeObject(id);
        
        return ResponseEntity.ok(ResponseDto.<TypeObjectResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Loại món ăn đã được kích hoạt thành công")
                .data(response)
                .build());
    }

    /**
     * Get TypeObject by name
     */
    @GetMapping("/read/name/{name}")
    @Operation(summary = "Get TypeObject by name", description = "Retrieve a specific TypeObject by its name")
    public ResponseEntity<ResponseDto<TypeObjectResponse>> getTypeObjectByName(@PathVariable String name) {
        log.info("Getting TypeObject by name: {}", name);
        
        TypeObjectResponse response = typeObjectsService.getTypeObjectByName(name);
        
        return ResponseEntity.ok(ResponseDto.<TypeObjectResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Lấy thông tin loại món ăn thành công")
                .data(response)
                .build());
    }

    // ============================================
    // ADVANCED SEARCH & FILTERING ENDPOINTS
    // ============================================

    /**
     * Advanced search with multiple criteria
     */
    @PostMapping("/search/advanced")
    @Operation(summary = "Advanced search", description = "Search TypeObjects with multiple criteria, sorting, and pagination")
    public ResponseEntity<ResponseDto<Page<TypeObjectResponse>>> advancedSearch(
            @Valid @RequestBody AdvancedSearchRequest request) {
        log.info("Advanced search with criteria: {}", request);
        
        Page<TypeObjectResponse> response = typeObjectsService.advancedSearch(request);
        
        return ResponseEntity.ok(ResponseDto.<Page<TypeObjectResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Tìm kiếm nâng cao thành công")
                .data(response)
                .build());
    }

    /**
     * Get TypeObjects by status with pagination
     */
    @GetMapping("/filter/status")
    @Operation(summary = "Filter by status", description = "Get TypeObjects filtered by active/inactive status")
    public ResponseEntity<ResponseDto<Page<TypeObjectResponse>>> getTypeObjectsByStatus(
            @RequestParam Boolean isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        log.info("Getting TypeObjects by status: {}", isActive);
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<TypeObjectResponse> response = typeObjectsService.getTypeObjectsByStatus(isActive, pageable);
        
        return ResponseEntity.ok(ResponseDto.<Page<TypeObjectResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Lấy danh sách loại món ăn theo trạng thái thành công")
                .data(response)
                .build());
    }

    /**
     * Get TypeObjects by date range
     */
    @GetMapping("/filter/date-range")
    @Operation(summary = "Filter by date range", description = "Get TypeObjects created within date range")
    public ResponseEntity<ResponseDto<Page<TypeObjectResponse>>> getTypeObjectsByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "created_at") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        log.info("Getting TypeObjects by date range: {} to {}", startDate, endDate);
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        java.time.LocalDateTime start = java.time.LocalDateTime.parse(startDate);
        java.time.LocalDateTime end = java.time.LocalDateTime.parse(endDate);
        
        Page<TypeObjectResponse> response = typeObjectsService.getTypeObjectsByDateRange(start, end, pageable);
        
        return ResponseEntity.ok(ResponseDto.<Page<TypeObjectResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Lấy danh sách loại món ăn theo khoảng thời gian thành công")
                .data(response)
                .build());
    }

    /**
     * Search TypeObjects by name with pagination
     */
    @GetMapping("/search/name")
    @Operation(summary = "Search by name with pagination", description = "Search TypeObjects by name pattern with pagination")
    public ResponseEntity<ResponseDto<Page<TypeObjectResponse>>> searchTypeObjectsByName(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        log.info("Searching TypeObjects by name: {} with pagination", name);
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<TypeObjectResponse> response = typeObjectsService.searchTypeObjectsByName(name, pageable);
        
        return ResponseEntity.ok(ResponseDto.<Page<TypeObjectResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Tìm kiếm loại món ăn theo tên thành công")
                .data(response)
                .build());
    }

    /**
     * Get TypeObjects by status and name
     */
    @GetMapping("/filter/status-and-name")
    @Operation(summary = "Filter by status and name", description = "Get TypeObjects filtered by both status and name pattern")
    public ResponseEntity<ResponseDto<Page<TypeObjectResponse>>> getTypeObjectsByStatusAndName(
            @RequestParam Boolean isActive,
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        log.info("Getting TypeObjects by status: {} and name: {}", isActive, name);
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<TypeObjectResponse> response = typeObjectsService.getTypeObjectsByStatusAndName(isActive, name, pageable);
        
        return ResponseEntity.ok(ResponseDto.<Page<TypeObjectResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Lấy danh sách loại món ăn theo trạng thái và tên thành công")
                .data(response)
                .build());
    }

    /**
     * Get recently created TypeObjects
     */
    @GetMapping("/recent/created")
    @Operation(summary = "Get recently created", description = "Get TypeObjects created in the last N days")
    public ResponseEntity<ResponseDto<List<TypeObjectResponse>>> getRecentlyCreated(
            @RequestParam(defaultValue = "7") int days) {
        log.info("Getting recently created TypeObjects for last {} days", days);
        
        List<TypeObjectResponse> response = typeObjectsService.getRecentlyCreated(days);
        
        return ResponseEntity.ok(ResponseDto.<List<TypeObjectResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Lấy danh sách loại món ăn mới tạo thành công")
                .data(response)
                .build());
    }

    /**
     * Get recently updated TypeObjects
     */
    @GetMapping("/recent/updated")
    @Operation(summary = "Get recently updated", description = "Get TypeObjects updated in the last N days")
    public ResponseEntity<ResponseDto<List<TypeObjectResponse>>> getRecentlyUpdated(
            @RequestParam(defaultValue = "7") int days) {
        log.info("Getting recently updated TypeObjects for last {} days", days);
        
        List<TypeObjectResponse> response = typeObjectsService.getRecentlyUpdated(days);
        
        return ResponseEntity.ok(ResponseDto.<List<TypeObjectResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Lấy danh sách loại món ăn mới cập nhật thành công")
                .data(response)
                .build());
    }

    /**
     * Get statistics
     */
    @GetMapping("/statistics")
    @Operation(summary = "Get statistics", description = "Get TypeObjects statistics (total, active, inactive)")
    public ResponseEntity<ResponseDto<Map<String, Long>>> getStatistics() {
        log.info("Getting TypeObjects statistics");
        
        Map<String, Long> response = typeObjectsService.getStatistics();
        
        return ResponseEntity.ok(ResponseDto.<Map<String, Long>>builder()
                .status(HttpStatus.OK.value())
                .message("Lấy thống kê loại món ăn thành công")
                .data(response)
                .build());
    }

    // ============================================
    // BULK OPERATIONS ENDPOINTS
    // ============================================

    /**
     * Bulk activate TypeObjects
     */
    @PutMapping("/bulk/activate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Bulk activate", description = "Activate multiple TypeObjects (Admin only)")
    public ResponseEntity<ResponseDto<List<TypeObjectResponse>>> bulkActivate(
            @RequestBody List<String> ids) {
        log.info("Bulk activating {} TypeObjects", ids.size());
        
        List<TypeObjectResponse> response = typeObjectsService.bulkActivate(ids);
        
        return ResponseEntity.ok(ResponseDto.<List<TypeObjectResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Kích hoạt hàng loạt loại món ăn thành công")
                .data(response)
                .build());
    }

    /**
     * Bulk deactivate TypeObjects
     */
    @PutMapping("/bulk/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Bulk deactivate", description = "Deactivate multiple TypeObjects (Admin only)")
    public ResponseEntity<ResponseDto<List<TypeObjectResponse>>> bulkDeactivate(
            @RequestBody List<String> ids) {
        log.info("Bulk deactivating {} TypeObjects", ids.size());
        
        List<TypeObjectResponse> response = typeObjectsService.bulkDeactivate(ids);
        
        return ResponseEntity.ok(ResponseDto.<List<TypeObjectResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Vô hiệu hóa hàng loạt loại món ăn thành công")
                .data(response)
                .build());
    }

    /**
     * Bulk delete TypeObjects
     */
    @DeleteMapping("/bulk/delete")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Bulk delete", description = "Delete multiple TypeObjects (Admin only)")
    public ResponseEntity<ResponseDto<Void>> bulkDelete(
            @RequestBody List<String> ids) {
        log.info("Bulk deleting {} TypeObjects", ids.size());
        
        typeObjectsService.bulkDelete(ids);
        
        return ResponseEntity.ok(ResponseDto.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Xóa hàng loạt loại món ăn thành công")
                .build());
    }

    /**
     * Execute bulk operation
     */
    @PostMapping("/bulk/execute")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Execute bulk operation", description = "Execute bulk operation (activate/deactivate/delete) (Admin only)")
    public ResponseEntity<ResponseDto<List<TypeObjectResponse>>> executeBulkOperation(
            @Valid @RequestBody BulkOperationRequest request) {
        log.info("Executing bulk operation: {} on {} items", request.getOperation(), request.getIds().size());
        
        List<TypeObjectResponse> response = typeObjectsService.executeBulkOperation(request);
        
        return ResponseEntity.ok(ResponseDto.<List<TypeObjectResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Thực hiện thao tác hàng loạt thành công")
                .data(response)
                .build());
    }
}
