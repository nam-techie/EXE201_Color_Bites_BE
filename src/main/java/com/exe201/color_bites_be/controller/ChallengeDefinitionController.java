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
@Tag(name = "ChallengeDefinitionController", description = "API quản lý các định nghĩa thử thách")
public class ChallengeDefinitionController {

    @Autowired
    private IChallengeDefinitionService challengeDefinitionService;

    @GetMapping
    @Operation(summary = "Lấy danh sách thử thách đang hoạt động", description = "Lấy tất cả thử thách đang hoạt động với phân trang")
    public ResponseEntity<Page<ChallengeDefinitionResponse>> getActiveChallenges(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ChallengeDefinitionResponse> challenges = challengeDefinitionService.readActiveChallenges(page, size);
        return ResponseEntity.ok(challenges);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy thử thách theo ID", description = "Lấy thông tin chi tiết của một thử thách")
    public ResponseEntity<ChallengeDefinitionResponse> getChallengeById(@PathVariable String id) {
        ChallengeDefinitionResponse challenge = challengeDefinitionService.readChallengeDefinitionById(id);
        return ResponseEntity.ok(challenge);
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Lấy danh sách thử thách theo loại", description = "Lấy các thử thách được lọc theo loại")
    public ResponseEntity<List<ChallengeDefinitionResponse>> getChallengesByType(@PathVariable ChallengeType type) {
        List<ChallengeDefinitionResponse> challenges = challengeDefinitionService.readChallengesByType(type);
        return ResponseEntity.ok(challenges);
    }

    @GetMapping("/restaurant/{restaurantId}")
    @Operation(summary = "Lấy danh sách thử thách theo nhà hàng", description = "Lấy các thử thách của một nhà hàng cụ thể")
    public ResponseEntity<List<ChallengeDefinitionResponse>> getChallengesByRestaurant(@PathVariable String restaurantId) {
        List<ChallengeDefinitionResponse> challenges = challengeDefinitionService.readChallengesByRestaurant(restaurantId);
        return ResponseEntity.ok(challenges);
    }


    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PARTNER')")
    @Operation(summary = "Tạo thử thách mới", description = "Tạo một thử thách mới (chỉ Admin/Partner)")
    public ResponseEntity<ChallengeDefinitionResponse> createChallenge(@Valid @RequestBody CreateChallengeDefinitionRequest request) {
        ChallengeDefinitionResponse challenge = challengeDefinitionService.createChallengeDefinition(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(challenge);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PARTNER')")
    @Operation(summary = "Cập nhật thử thách", description = "Cập nhật một thử thách đã tồn tại (chỉ Admin/Partner)")
    public ResponseEntity<ChallengeDefinitionResponse> updateChallenge(
            @PathVariable String id, 
            @Valid @RequestBody UpdateChallengeDefinitionRequest request) {
        ChallengeDefinitionResponse challenge = challengeDefinitionService.updateChallengeDefinition(id, request);
        return ResponseEntity.ok(challenge);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PARTNER')")
    @Operation(summary = "Xóa thử thách", description = "Xóa một thử thách (chỉ Admin/Partner)")
    public ResponseEntity<Void> deleteChallenge(@PathVariable String id) {
        challengeDefinitionService.deleteChallengeDefinition(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/activate")
    @PreAuthorize("hasAnyRole('ADMIN', 'PARTNER')")
    @Operation(summary = "Kích hoạt thử thách", description = "Kích hoạt một thử thách (chỉ Admin/Partner)")
    public ResponseEntity<Void> activateChallenge(@PathVariable String id) {
        challengeDefinitionService.activateChallenge(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('ADMIN', 'PARTNER')")
    @Operation(summary = "Vô hiệu hóa thử thách", description = "Vô hiệu hóa một thử thách (chỉ Admin/Partner)")
    public ResponseEntity<Void> deactivateChallenge(@PathVariable String id) {
        challengeDefinitionService.deactivateChallenge(id);
        return ResponseEntity.ok().build();
    }
}


