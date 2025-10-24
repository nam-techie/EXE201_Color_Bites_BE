package com.exe201.color_bites_be.controller;

import com.exe201.color_bites_be.dto.request.SubmitChallengeEntryRequest;
import com.exe201.color_bites_be.dto.request.UpdateChallengeEntryRequest;
import com.exe201.color_bites_be.dto.response.ChallengeEntryResponse;
import com.exe201.color_bites_be.enums.EntryStatus;
import com.exe201.color_bites_be.service.IChallengeEntryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/challenges")
@Tag(name = "Quản lý Bài nộp Thử thách", description = "API quản lý các bài nộp trong thử thách")
public class ChallengeEntryController {

    @Autowired
    private IChallengeEntryService entryService;

    @PostMapping("/participations/{participationId}/entries")
    @Operation(summary = "Nộp bài thử thách", description = "Nộp một bài tham gia thử thách")
    public ResponseEntity<ChallengeEntryResponse> submitEntry(
            @PathVariable String participationId, 
            @Valid @RequestBody SubmitChallengeEntryRequest request) {
        ChallengeEntryResponse entry = entryService.submitEntry(participationId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(entry);
    }

    @GetMapping("/entries/{entryId}")
    @Operation(summary = "Lấy bài nộp theo ID", description = "Lấy thông tin chi tiết của một bài nộp")
    public ResponseEntity<ChallengeEntryResponse> getEntryById(@PathVariable String entryId) {
        ChallengeEntryResponse entry = entryService.readEntryById(entryId);
        return ResponseEntity.ok(entry);
    }

    @GetMapping("/participations/{participationId}/entries")
    @Operation(summary = "Lấy danh sách bài nộp theo tham gia", description = "Lấy tất cả bài nộp của một lần tham gia thử thách")
    public ResponseEntity<List<ChallengeEntryResponse>> getEntriesByParticipation(@PathVariable String participationId) {
        List<ChallengeEntryResponse> entries = entryService.readEntriesByParticipation(participationId);
        return ResponseEntity.ok(entries);
    }

    @GetMapping("/participations/{participationId}/entries/paged")
    @Operation(summary = "Lấy danh sách bài nộp theo tham gia (có phân trang)", description = "Lấy danh sách bài nộp có phân trang của một lần tham gia thử thách")
    public ResponseEntity<Page<ChallengeEntryResponse>> getEntriesByParticipationPaged(
            @PathVariable String participationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ChallengeEntryResponse> entries = entryService.readEntriesByParticipation(participationId, page, size);
        return ResponseEntity.ok(entries);
    }

    @GetMapping("/entries/restaurant/{restaurantId}")
    @Operation(summary = "Lấy danh sách bài nộp theo nhà hàng", description = "Lấy tất cả bài nộp của một nhà hàng cụ thể")
    public ResponseEntity<List<ChallengeEntryResponse>> getEntriesByRestaurant(@PathVariable String restaurantId) {
        List<ChallengeEntryResponse> entries = entryService.readEntriesByRestaurant(restaurantId);
        return ResponseEntity.ok(entries);
    }

    @GetMapping("/entries/status/{status}")
    @Operation(summary = "Lấy danh sách bài nộp theo trạng thái", description = "Lấy tất cả bài nộp được lọc theo trạng thái")
    public ResponseEntity<List<ChallengeEntryResponse>> getEntriesByStatus(@PathVariable EntryStatus status) {
        List<ChallengeEntryResponse> entries = entryService.readEntriesByStatus(status);
        return ResponseEntity.ok(entries);
    }

    @GetMapping("/entries/status/{status}/paged")
    @Operation(summary = "Lấy danh sách bài nộp theo trạng thái (có phân trang)", description = "Lấy danh sách bài nộp có phân trang được lọc theo trạng thái")
    public ResponseEntity<Page<ChallengeEntryResponse>> getEntriesByStatusPaged(
            @PathVariable EntryStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ChallengeEntryResponse> entries = entryService.readEntriesByStatus(status, page, size);
        return ResponseEntity.ok(entries);
    }

    @PutMapping("/entries/{entryId}")
    @Operation(summary = "Cập nhật bài nộp", description = "Cập nhật một bài nộp đã tồn tại")
    public ResponseEntity<ChallengeEntryResponse> updateEntry(
            @PathVariable String entryId, 
            @Valid @RequestBody UpdateChallengeEntryRequest request) {
        ChallengeEntryResponse entry = entryService.updateEntry(entryId, request);
        return ResponseEntity.ok(entry);
    }

    @PutMapping("/entries/{entryId}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'PARTNER')")
    @Operation(summary = "Duyệt bài nộp", description = "Duyệt một bài nộp thử thách (chỉ Admin/Partner)")
    public ResponseEntity<ChallengeEntryResponse> approveEntry(@PathVariable String entryId) {
        ChallengeEntryResponse entry = entryService.approveEntry(entryId);
        return ResponseEntity.ok(entry);
    }

    @PutMapping("/entries/{entryId}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'PARTNER')")
    @Operation(summary = "Từ chối bài nộp", description = "Từ chối một bài nộp thử thách (chỉ Admin/Partner)")
    public ResponseEntity<ChallengeEntryResponse> rejectEntry(@PathVariable String entryId) {
        ChallengeEntryResponse entry = entryService.rejectEntry(entryId);
        return ResponseEntity.ok(entry);
    }

    @DeleteMapping("/entries/{entryId}")
    @Operation(summary = "Xóa bài nộp", description = "Xóa một bài nộp thử thách")
    public ResponseEntity<Void> deleteEntry(@PathVariable String entryId) {
        entryService.deleteEntry(entryId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/participations/{participationId}/entries/count")
    @Operation(summary = "Lấy số lượng bài nộp", description = "Lấy tổng số bài nộp của một lần tham gia")
    public ResponseEntity<Long> getEntryCount(@PathVariable String participationId) {
        long count = entryService.countEntriesByParticipation(participationId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/participations/{participationId}/entries/approved-count")
    @Operation(summary = "Lấy số lượng bài nộp đã duyệt", description = "Lấy số lượng bài nộp đã được duyệt của một lần tham gia")
    public ResponseEntity<Long> getApprovedEntryCount(@PathVariable String participationId) {
        long count = entryService.countApprovedEntriesByParticipation(participationId);
        return ResponseEntity.ok(count);
    }
}


