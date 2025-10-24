package com.exe201.color_bites_be.controller;

import com.exe201.color_bites_be.dto.request.CreateChallengeDefinitionRequest;
import com.exe201.color_bites_be.dto.request.UpdateChallengeDefinitionRequest;
import com.exe201.color_bites_be.dto.response.ChallengeDefinitionResponse;
import com.exe201.color_bites_be.enums.ChallengeType;
import com.exe201.color_bites_be.service.IChallengeDefinitionService;
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
@Tag(name = "Challenge Definition Management", description = "APIs for managing challenge definitions")
public class ChallengeDefinitionController {

    @Autowired
    private IChallengeDefinitionService challengeDefinitionService;

    @GetMapping
    @Operation(summary = "Get active challenges", description = "Retrieve all active challenges with pagination")
    public ResponseEntity<Page<ChallengeDefinitionResponse>> getActiveChallenges(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ChallengeDefinitionResponse> challenges = challengeDefinitionService.readActiveChallenges(page, size);
        return ResponseEntity.ok(challenges);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get challenge by ID", description = "Retrieve a specific challenge by its ID")
    public ResponseEntity<ChallengeDefinitionResponse> getChallengeById(@PathVariable String id) {
        ChallengeDefinitionResponse challenge = challengeDefinitionService.readChallengeDefinitionById(id);
        return ResponseEntity.ok(challenge);
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Get challenges by type", description = "Retrieve challenges filtered by type")
    public ResponseEntity<List<ChallengeDefinitionResponse>> getChallengesByType(@PathVariable ChallengeType type) {
        List<ChallengeDefinitionResponse> challenges = challengeDefinitionService.readChallengesByType(type);
        return ResponseEntity.ok(challenges);
    }

    @GetMapping("/restaurant/{restaurantId}")
    @Operation(summary = "Get challenges by restaurant", description = "Retrieve challenges for a specific restaurant")
    public ResponseEntity<List<ChallengeDefinitionResponse>> getChallengesByRestaurant(@PathVariable String restaurantId) {
        List<ChallengeDefinitionResponse> challenges = challengeDefinitionService.readChallengesByRestaurant(restaurantId);
        return ResponseEntity.ok(challenges);
    }


    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PARTNER')")
    @Operation(summary = "Create challenge", description = "Create a new challenge (Admin/Partner only)")
    public ResponseEntity<ChallengeDefinitionResponse> createChallenge(@Valid @RequestBody CreateChallengeDefinitionRequest request) {
        ChallengeDefinitionResponse challenge = challengeDefinitionService.createChallengeDefinition(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(challenge);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PARTNER')")
    @Operation(summary = "Update challenge", description = "Update an existing challenge (Admin/Partner only)")
    public ResponseEntity<ChallengeDefinitionResponse> updateChallenge(
            @PathVariable String id, 
            @Valid @RequestBody UpdateChallengeDefinitionRequest request) {
        ChallengeDefinitionResponse challenge = challengeDefinitionService.updateChallengeDefinition(id, request);
        return ResponseEntity.ok(challenge);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PARTNER')")
    @Operation(summary = "Delete challenge", description = "Delete a challenge (Admin/Partner only)")
    public ResponseEntity<Void> deleteChallenge(@PathVariable String id) {
        challengeDefinitionService.deleteChallengeDefinition(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/activate")
    @PreAuthorize("hasAnyRole('ADMIN', 'PARTNER')")
    @Operation(summary = "Activate challenge", description = "Activate a challenge (Admin/Partner only)")
    public ResponseEntity<Void> activateChallenge(@PathVariable String id) {
        challengeDefinitionService.activateChallenge(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('ADMIN', 'PARTNER')")
    @Operation(summary = "Deactivate challenge", description = "Deactivate a challenge (Admin/Partner only)")
    public ResponseEntity<Void> deactivateChallenge(@PathVariable String id) {
        challengeDefinitionService.deactivateChallenge(id);
        return ResponseEntity.ok().build();
    }
}


