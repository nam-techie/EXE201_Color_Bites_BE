package com.exe201.color_bites_be.controller;

import com.exe201.color_bites_be.dto.request.*;
import com.exe201.color_bites_be.dto.response.ResponseDto;
import com.exe201.color_bites_be.dto.response.TypeObjectResponse;
import com.exe201.color_bites_be.entity.TypeObjects;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/type-objects")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Type Objects Management", description = "APIs for managing global food type catalog")
public class TypeObjectsController {

    private final ITypeObjectsService typeObjectsService;

    /**
     * Tạo loại món ăn mới
     */
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Tạo loại món ăn", description = "Tạo loại món ăn mới (Chỉ Admin)")
    public ResponseEntity<ResponseDto<TypeObjectResponse>> createTypeObject(
            @Valid @RequestPart("name") String name,
            @RequestPart(value = "file", required = false)MultipartFile file) {
        TypeObjectResponse response = typeObjectsService.createTypeObject(name, file);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDto.<TypeObjectResponse>builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Loại món ăn đã được tạo thành công")
                        .data(response)
                        .build());
    }

    /**
     * Lấy thông tin loại món ăn theo ID
     */
    @GetMapping("/read/{id}")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @Operation(summary = "Lấy thông tin theo ID", description = "Lấy thông tin chi tiết loại món ăn theo ID")
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
     * Lấy danh sách tất cả loại món ăn với phân trang
     */
    @GetMapping("/list")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @Operation(summary = "Lấy danh sách tất cả", description = "Lấy danh sách tất cả loại món ăn với phân trang")
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
     * Lấy danh sách loại món ăn đang hoạt động
     */

    @GetMapping("/read/active")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @Operation(summary = "Lấy danh sách đang hoạt động", description = "Lấy danh sách tất cả loại món ăn đang hoạt động")
    public ResponseEntity<ResponseDto<List<TypeObjectResponse>>> getActiveTypeObjects() {
        log.info("Getting all active TypeObjects");
        
        List<TypeObjectResponse> response = typeObjectsService.getActiveTypeObjects();
        
        return ResponseEntity.ok(ResponseDto.<List<TypeObjectResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Lấy danh sách loại món ăn đang hoạt động thành công")
                .data(response)
                .build());
    }

    @GetMapping("/read/random")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @Operation(summary = "Lấy danh sách 10 loại món ăn", description = "Lấy danh sách 10 loại món ăn ngẫu nhiên")
    public ResponseEntity<ResponseDto<List<TypeObjects>>> getRandomTypeObjects() {
        log.info("Getting all active TypeObjects");

        List<TypeObjects> response = typeObjectsService.getRandomFoods();

        return ResponseEntity.ok(ResponseDto.<List<TypeObjects>>builder()
                .status(HttpStatus.OK.value())
                .message("Lấy danh sách 10 loại món ăn đang hoạt động thành công")
                .data(response)
                .build());
    }

    /**
     * Tìm kiếm loại món ăn theo tên
     */
    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @Operation(summary = "Tìm kiếm theo tên", description = "Tìm kiếm loại món ăn theo từ khóa tên")
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
     * Cập nhật loại món ăn
     */
    @PutMapping("/edit/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Cập nhật loại món ăn", description = "Cập nhật thông tin loại món ăn (Chỉ Admin)")
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
     * Xóa loại món ăn (xóa mềm)
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Xóa loại món ăn", description = "Xóa loại món ăn (xóa mềm - chỉ vô hiệu hóa) (Chỉ Admin)")
    public ResponseEntity<ResponseDto<Void>> deleteTypeObject(@PathVariable String id) {
        log.info("Deleting TypeObject with ID: {}", id);
        
        typeObjectsService.deleteTypeObject(id);
        
        return ResponseEntity.ok(ResponseDto.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Loại món ăn đã được xóa thành công")
                .build());
    }

    /**
     * Kích hoạt loại món ăn
     */
    @PutMapping("/activate/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Kích hoạt loại món ăn", description = "Kích hoạt loại món ăn (Chỉ Admin)")
    public ResponseEntity<ResponseDto<TypeObjectResponse>> activateTypeObject(@PathVariable String id) {
        log.info("Activating TypeObject with ID: {}", id);
        
        TypeObjectResponse response = typeObjectsService.activateTypeObject(id);
        
        return ResponseEntity.ok(ResponseDto.<TypeObjectResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Loại món ăn đã được kích hoạt thành công")
                .data(response)
                .build());
    }

//    /**
//     * Lấy thông tin loại món ăn theo tên
//     */
//    @GetMapping("/read/name/{name}")
//    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
//    @Operation(summary = "Lấy thông tin theo tên", description = "Lấy thông tin chi tiết loại món ăn theo tên")
//    public ResponseEntity<ResponseDto<TypeObjectResponse>> getTypeObjectByName(@PathVariable String name) {
//        log.info("Getting TypeObject by name: {}", name);
//
//        TypeObjectResponse response = typeObjectsService.getTypeObjectByName(name);
//
//        return ResponseEntity.ok(ResponseDto.<TypeObjectResponse>builder()
//                .status(HttpStatus.OK.value())
//                .message("Lấy thông tin loại món ăn thành công")
//                .data(response)
//                .build());
//    }

    // ============================================
    // ADVANCED SEARCH & FILTERING ENDPOINTS
    // ============================================

    /**
     * Tìm kiếm nâng cao với nhiều tiêu chí
     */
    @PostMapping("/search/advanced")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @Operation(summary = "Tìm kiếm nâng cao", description = "Tìm kiếm loại món ăn với nhiều tiêu chí, sắp xếp và phân trang")
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

//    /**
//     * Lấy danh sách loại món ăn theo trạng thái với phân trang
//     */
//    @GetMapping("/filter/status")
//    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
//    @Operation(summary = "Lọc theo trạng thái", description = "Lấy danh sách loại món ăn được lọc theo trạng thái hoạt động/không hoạt động")
//    public ResponseEntity<ResponseDto<Page<TypeObjectResponse>>> getTypeObjectsByStatus(
//            @RequestParam Boolean isActive,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "20") int size,
//            @RequestParam(defaultValue = "name") String sortBy,
//            @RequestParam(defaultValue = "asc") String sortDirection) {
//        log.info("Getting TypeObjects by status: {}", isActive);
//
//        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
//        Pageable pageable = PageRequest.of(page, size, sort);
//
//        Page<TypeObjectResponse> response = typeObjectsService.getTypeObjectsByStatus(isActive, pageable);
//
//        return ResponseEntity.ok(ResponseDto.<Page<TypeObjectResponse>>builder()
//                .status(HttpStatus.OK.value())
//                .message("Lấy danh sách loại món ăn theo trạng thái thành công")
//                .data(response)
//                .build());
//    }

//    /**
//     * Lấy danh sách loại món ăn theo khoảng thời gian
//     */
//    @GetMapping("/filter/date-range")
//    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
//    @Operation(summary = "Lọc theo khoảng thời gian", description = "Lấy danh sách loại món ăn được tạo trong khoảng thời gian chỉ định")
//    public ResponseEntity<ResponseDto<Page<TypeObjectResponse>>> getTypeObjectsByDateRange(
//            @RequestParam String startDate,
//            @RequestParam String endDate,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "20") int size,
//            @RequestParam(defaultValue = "created_at") String sortBy,
//            @RequestParam(defaultValue = "desc") String sortDirection) {
//        log.info("Getting TypeObjects by date range: {} to {}", startDate, endDate);
//
//        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
//        Pageable pageable = PageRequest.of(page, size, sort);
//
//        java.time.LocalDateTime start = java.time.LocalDateTime.parse(startDate);
//        java.time.LocalDateTime end = java.time.LocalDateTime.parse(endDate);
//
//        Page<TypeObjectResponse> response = typeObjectsService.getTypeObjectsByDateRange(start, end, pageable);
//
//        return ResponseEntity.ok(ResponseDto.<Page<TypeObjectResponse>>builder()
//                .status(HttpStatus.OK.value())
//                .message("Lấy danh sách loại món ăn theo khoảng thời gian thành công")
//                .data(response)
//                .build());
//    }

    /**
     * Tìm kiếm loại món ăn theo tên với phân trang
     */
    @GetMapping("/search/name")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @Operation(summary = "Tìm kiếm theo tên với phân trang", description = "Tìm kiếm loại món ăn theo mẫu tên với phân trang")
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

//    /**
//     * Lấy danh sách loại món ăn theo trạng thái và tên
//     */
//    @GetMapping("/filter/status-and-name")
//    @Operation(summary = "Lọc theo trạng thái và tên", description = "Lấy danh sách loại món ăn được lọc theo cả trạng thái và mẫu tên")
//    public ResponseEntity<ResponseDto<Page<TypeObjectResponse>>> getTypeObjectsByStatusAndName(
//            @RequestParam Boolean isActive,
//            @RequestParam String name,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "20") int size,
//            @RequestParam(defaultValue = "name") String sortBy,
//            @RequestParam(defaultValue = "asc") String sortDirection) {
//        log.info("Getting TypeObjects by status: {} and name: {}", isActive, name);
//
//        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
//        Pageable pageable = PageRequest.of(page, size, sort);
//
//        Page<TypeObjectResponse> response = typeObjectsService.getTypeObjectsByStatusAndName(isActive, name, pageable);
//
//        return ResponseEntity.ok(ResponseDto.<Page<TypeObjectResponse>>builder()
//                .status(HttpStatus.OK.value())
//                .message("Lấy danh sách loại món ăn theo trạng thái và tên thành công")
//                .data(response)
//                .build());
//    }

    /**
     * Lấy danh sách loại món ăn mới tạo gần đây
     */
    @GetMapping("/recent/created")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @Operation(summary = "Lấy danh sách mới tạo", description = "Lấy danh sách loại món ăn được tạo trong N ngày gần đây")
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

//    /**
//     * Lấy danh sách loại món ăn mới cập nhật gần đây
//     */
//    @GetMapping("/recent/updated")
//    @Operation(summary = "Lấy danh sách mới cập nhật", description = "Lấy danh sách loại món ăn được cập nhật trong N ngày gần đây")
//    public ResponseEntity<ResponseDto<List<TypeObjectResponse>>> getRecentlyUpdated(
//            @RequestParam(defaultValue = "7") int days) {
//        log.info("Getting recently updated TypeObjects for last {} days", days);
//
//        List<TypeObjectResponse> response = typeObjectsService.getRecentlyUpdated(days);
//
//        return ResponseEntity.ok(ResponseDto.<List<TypeObjectResponse>>builder()
//                .status(HttpStatus.OK.value())
//                .message("Lấy danh sách loại món ăn mới cập nhật thành công")
//                .data(response)
//                .build());
//    }

    /**
     * Lấy thống kê loại món ăn
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Lấy thống kê", description = "Lấy thống kê loại món ăn (tổng số, đang hoạt động, không hoạt động)")
    public ResponseEntity<ResponseDto<Map<String, Long>>> getStatistics() {
        log.info("Getting TypeObjects statistics");
        
        Map<String, Long> response = typeObjectsService.getStatistics();
        
        return ResponseEntity.ok(ResponseDto.<Map<String, Long>>builder()
                .status(HttpStatus.OK.value())
                .message("Lấy thống kê loại món ăn thành công")
                .data(response)
                .build());
    }

//    // ============================================
//    // BULK OPERATIONS ENDPOINTS
//    // ============================================
//
//    /**
//     * Kích hoạt hàng loạt loại món ăn
//     */
//    @PutMapping("/bulk/activate")
//    @PreAuthorize("hasRole('ADMIN')")
//    @Operation(summary = "Kích hoạt hàng loạt", description = "Kích hoạt nhiều loại món ăn cùng lúc (Chỉ Admin)")
//    public ResponseEntity<ResponseDto<List<TypeObjectResponse>>> bulkActivate(
//            @RequestBody List<String> ids) {
//        log.info("Bulk activating {} TypeObjects", ids.size());
//
//        List<TypeObjectResponse> response = typeObjectsService.bulkActivate(ids);
//
//        return ResponseEntity.ok(ResponseDto.<List<TypeObjectResponse>>builder()
//                .status(HttpStatus.OK.value())
//                .message("Kích hoạt hàng loạt loại món ăn thành công")
//                .data(response)
//                .build());
//    }
//
//    /**
//     * Vô hiệu hóa hàng loạt loại món ăn
//     */
//    @PutMapping("/bulk/deactivate")
//    @PreAuthorize("hasRole('ADMIN')")
//    @Operation(summary = "Vô hiệu hóa hàng loạt", description = "Vô hiệu hóa nhiều loại món ăn cùng lúc (Chỉ Admin)")
//    public ResponseEntity<ResponseDto<List<TypeObjectResponse>>> bulkDeactivate(
//            @RequestBody List<String> ids) {
//        log.info("Bulk deactivating {} TypeObjects", ids.size());
//
//        List<TypeObjectResponse> response = typeObjectsService.bulkDeactivate(ids);
//
//        return ResponseEntity.ok(ResponseDto.<List<TypeObjectResponse>>builder()
//                .status(HttpStatus.OK.value())
//                .message("Vô hiệu hóa hàng loạt loại món ăn thành công")
//                .data(response)
//                .build());
//    }

//    /**
//     * Xóa hàng loạt loại món ăn
//     */
//    @DeleteMapping("/bulk/delete")
//    @PreAuthorize("hasRole('ADMIN')")
//    @Operation(summary = "Xóa hàng loạt", description = "Xóa nhiều loại món ăn cùng lúc (Chỉ Admin)")
//    public ResponseEntity<ResponseDto<Void>> bulkDelete(
//            @RequestBody List<String> ids) {
//        log.info("Bulk deleting {} TypeObjects", ids.size());
//
//        typeObjectsService.bulkDelete(ids);
//
//        return ResponseEntity.ok(ResponseDto.<Void>builder()
//                .status(HttpStatus.OK.value())
//                .message("Xóa hàng loạt loại món ăn thành công")
//                .build());
//    }

//    /**
//     * Thực hiện thao tác hàng loạt
//     */
//    @PostMapping("/bulk/execute")
//    @PreAuthorize("hasRole('ADMIN')")
//    @Operation(summary = "Thực hiện thao tác hàng loạt", description = "Thực hiện thao tác hàng loạt (kích hoạt/vô hiệu hóa/xóa) (Chỉ Admin)")
//    public ResponseEntity<ResponseDto<List<TypeObjectResponse>>> executeBulkOperation(
//            @Valid @RequestBody BulkOperationRequest request) {
//        log.info("Executing bulk operation: {} on {} items", request.getOperation(), request.getIds().size());
//
//        List<TypeObjectResponse> response = typeObjectsService.executeBulkOperation(request);
//
//        return ResponseEntity.ok(ResponseDto.<List<TypeObjectResponse>>builder()
//                .status(HttpStatus.OK.value())
//                .message("Thực hiện thao tác hàng loạt thành công")
//                .data(response)
//                .build());
//    }
}
