package com.exe201.color_bites_be.controller;

import com.exe201.color_bites_be.dto.request.JoinChallengeRequest;
import com.exe201.color_bites_be.dto.response.ChallengeParticipationResponse;
import com.exe201.color_bites_be.dto.response.ChallengeProgressResponse;
import com.exe201.color_bites_be.enums.ParticipationStatus;
import com.exe201.color_bites_be.service.IChallengeParticipationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/challenges")
@Tag(name = "Challenge Participation Management", description = "APIs for managing challenge participations")
public class ChallengeParticipationController {

    @Autowired
    private IChallengeParticipationService participationService;

    @PostMapping("/{challengeId}/join")
    @Operation(summary = "Join challenge", description = "Join a challenge")
    public ResponseEntity<ChallengeParticipationResponse> joinChallenge(
            @PathVariable String challengeId, 
            @Valid @RequestBody JoinChallengeRequest request) {
        ChallengeParticipationResponse participation = participationService.joinChallenge(challengeId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(participation);
    }

    @GetMapping("/participations/{participationId}")
    @Operation(summary = "Get participation by ID", description = "Retrieve a specific participation by its ID")
    public ResponseEntity<ChallengeParticipationResponse> getParticipationById(@PathVariable String participationId) {
        ChallengeParticipationResponse participation = participationService.readParticipationById(participationId);
        return ResponseEntity.ok(participation);
    }

    @GetMapping("/my-participations")
    @Operation(summary = "Get user participations", description = "Get all participations for the current user")
    public ResponseEntity<List<ChallengeParticipationResponse>> getMyParticipations() {
        // TODO: Get current user ID from security context
        String currentUserId = "current-user-id"; // Placeholder
        List<ChallengeParticipationResponse> participations = participationService.readUserParticipations(currentUserId);
        return ResponseEntity.ok(participations);
    }

    @GetMapping("/my-participations/paged")
    @Operation(summary = "Get user participations (paginated)", description = "Get paginated participations for the current user")
    public ResponseEntity<Page<ChallengeParticipationResponse>> getMyParticipationsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        // TODO: Get current user ID from security context
        String currentUserId = "current-user-id"; // Placeholder
        Page<ChallengeParticipationResponse> participations = participationService.readUserParticipations(currentUserId, page, size);
        return ResponseEntity.ok(participations);
    }

    @GetMapping("/{challengeId}/participations")
    @Operation(summary = "Get participations by challenge", description = "Get all participations for a specific challenge")
    public ResponseEntity<List<ChallengeParticipationResponse>> getParticipationsByChallenge(@PathVariable String challengeId) {
        List<ChallengeParticipationResponse> participations = participationService.readParticipationsByChallenge(challengeId);
        return ResponseEntity.ok(participations);
    }

    @GetMapping("/participations/status/{status}")
    @Operation(summary = "Get participations by status", description = "Get all participations filtered by status")
    public ResponseEntity<List<ChallengeParticipationResponse>> getParticipationsByStatus(@PathVariable ParticipationStatus status) {
        List<ChallengeParticipationResponse> participations = participationService.readParticipationsByStatus(status);
        return ResponseEntity.ok(participations);
    }

    @GetMapping("/participations/{participationId}/progress")
    @Operation(summary = "Get participation progress", description = "Get progress details for a specific participation")
    public ResponseEntity<ChallengeProgressResponse> getParticipationProgress(@PathVariable String participationId) {
        ChallengeProgressResponse progress = participationService.getParticipationProgress(participationId);
        return ResponseEntity.ok(progress);
    }

    @PutMapping("/participations/{participationId}/progress")
    @Operation(summary = "Update participation progress", description = "Update progress count for a participation")
    public ResponseEntity<Void> updateParticipationProgress(
            @PathVariable String participationId, 
            @RequestParam int progressCount) {
        participationService.updateParticipationProgress(participationId, progressCount);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/participations/{participationId}/complete")
    @Operation(summary = "Complete participation", description = "Mark a participation as completed")
    public ResponseEntity<Void> completeParticipation(@PathVariable String participationId) {
        participationService.completeParticipation(participationId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/participations/{participationId}/fail")
    @Operation(summary = "Fail participation", description = "Mark a participation as failed")
    public ResponseEntity<Void> failParticipation(@PathVariable String participationId) {
        participationService.failParticipation(participationId);
        return ResponseEntity.ok().build();
    }
}


