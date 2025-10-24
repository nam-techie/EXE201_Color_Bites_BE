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
@Tag(name = "Challenge Entry Management", description = "APIs for managing challenge entries")
public class ChallengeEntryController {

    @Autowired
    private IChallengeEntryService entryService;

    @PostMapping("/participations/{participationId}/entries")
    @Operation(summary = "Submit challenge entry", description = "Submit an entry for a challenge participation")
    public ResponseEntity<ChallengeEntryResponse> submitEntry(
            @PathVariable String participationId, 
            @Valid @RequestBody SubmitChallengeEntryRequest request) {
        ChallengeEntryResponse entry = entryService.submitEntry(participationId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(entry);
    }

    @GetMapping("/entries/{entryId}")
    @Operation(summary = "Get entry by ID", description = "Retrieve a specific entry by its ID")
    public ResponseEntity<ChallengeEntryResponse> getEntryById(@PathVariable String entryId) {
        ChallengeEntryResponse entry = entryService.readEntryById(entryId);
        return ResponseEntity.ok(entry);
    }

    @GetMapping("/participations/{participationId}/entries")
    @Operation(summary = "Get entries by participation", description = "Get all entries for a specific participation")
    public ResponseEntity<List<ChallengeEntryResponse>> getEntriesByParticipation(@PathVariable String participationId) {
        List<ChallengeEntryResponse> entries = entryService.readEntriesByParticipation(participationId);
        return ResponseEntity.ok(entries);
    }

    @GetMapping("/participations/{participationId}/entries/paged")
    @Operation(summary = "Get entries by participation (paginated)", description = "Get paginated entries for a specific participation")
    public ResponseEntity<Page<ChallengeEntryResponse>> getEntriesByParticipationPaged(
            @PathVariable String participationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ChallengeEntryResponse> entries = entryService.readEntriesByParticipation(participationId, page, size);
        return ResponseEntity.ok(entries);
    }

    @GetMapping("/entries/restaurant/{restaurantId}")
    @Operation(summary = "Get entries by restaurant", description = "Get all entries for a specific restaurant")
    public ResponseEntity<List<ChallengeEntryResponse>> getEntriesByRestaurant(@PathVariable String restaurantId) {
        List<ChallengeEntryResponse> entries = entryService.readEntriesByRestaurant(restaurantId);
        return ResponseEntity.ok(entries);
    }

    @GetMapping("/entries/status/{status}")
    @Operation(summary = "Get entries by status", description = "Get all entries filtered by status")
    public ResponseEntity<List<ChallengeEntryResponse>> getEntriesByStatus(@PathVariable EntryStatus status) {
        List<ChallengeEntryResponse> entries = entryService.readEntriesByStatus(status);
        return ResponseEntity.ok(entries);
    }

    @GetMapping("/entries/status/{status}/paged")
    @Operation(summary = "Get entries by status (paginated)", description = "Get paginated entries filtered by status")
    public ResponseEntity<Page<ChallengeEntryResponse>> getEntriesByStatusPaged(
            @PathVariable EntryStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ChallengeEntryResponse> entries = entryService.readEntriesByStatus(status, page, size);
        return ResponseEntity.ok(entries);
    }

    @PutMapping("/entries/{entryId}")
    @Operation(summary = "Update entry", description = "Update an existing entry")
    public ResponseEntity<ChallengeEntryResponse> updateEntry(
            @PathVariable String entryId, 
            @Valid @RequestBody UpdateChallengeEntryRequest request) {
        ChallengeEntryResponse entry = entryService.updateEntry(entryId, request);
        return ResponseEntity.ok(entry);
    }

    @PutMapping("/entries/{entryId}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'PARTNER')")
    @Operation(summary = "Approve entry", description = "Approve a challenge entry (Admin/Partner only)")
    public ResponseEntity<ChallengeEntryResponse> approveEntry(@PathVariable String entryId) {
        ChallengeEntryResponse entry = entryService.approveEntry(entryId);
        return ResponseEntity.ok(entry);
    }

    @PutMapping("/entries/{entryId}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'PARTNER')")
    @Operation(summary = "Reject entry", description = "Reject a challenge entry (Admin/Partner only)")
    public ResponseEntity<ChallengeEntryResponse> rejectEntry(@PathVariable String entryId) {
        ChallengeEntryResponse entry = entryService.rejectEntry(entryId);
        return ResponseEntity.ok(entry);
    }

    @DeleteMapping("/entries/{entryId}")
    @Operation(summary = "Delete entry", description = "Delete a challenge entry")
    public ResponseEntity<Void> deleteEntry(@PathVariable String entryId) {
        entryService.deleteEntry(entryId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/participations/{participationId}/entries/count")
    @Operation(summary = "Get entry count", description = "Get total number of entries for a participation")
    public ResponseEntity<Long> getEntryCount(@PathVariable String participationId) {
        long count = entryService.countEntriesByParticipation(participationId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/participations/{participationId}/entries/approved-count")
    @Operation(summary = "Get approved entry count", description = "Get number of approved entries for a participation")
    public ResponseEntity<Long> getApprovedEntryCount(@PathVariable String participationId) {
        long count = entryService.countApprovedEntriesByParticipation(participationId);
        return ResponseEntity.ok(count);
    }
}


