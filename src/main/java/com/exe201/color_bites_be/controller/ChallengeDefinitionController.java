package com.exe201.color_bites_be.controller;

import com.exe201.color_bites_be.dto.request.CreateChallengeDefinitionRequest;
import com.exe201.color_bites_be.dto.request.UpdateChallengeDefinitionRequest;
import com.exe201.color_bites_be.dto.response.ChallengeDefinitionResponse;
import com.exe201.color_bites_be.dto.response.ChallengeDetailResponse;
import com.exe201.color_bites_be.dto.response.ResponseDto;
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
@PreAuthorize("hasAnyAuthority('ADMIN', 'PARTNER', 'USER')")
@Tag(name = "ChallengeDefinitionController", description = "API quản lý các định nghĩa thử thách")
public class ChallengeDefinitionController {

    @Autowired
    private IChallengeDefinitionService challengeDefinitionService;

    @GetMapping
    @Operation(summary = "Lấy danh sách thử thách đang hoạt động", description = "Lấy tất cả thử thách đang hoạt động với phân trang")
    public ResponseEntity<ResponseDto<List<ChallengeDetailResponse>>> getActiveChallenges() {
        List<ChallengeDetailResponse> challenges = challengeDefinitionService.readActiveChallenges();
        return ResponseEntity.ok(ResponseDto.<List<ChallengeDetailResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Lấy danh sách thử thách đang hoạt động thành công")
                .data(challenges)
                .build());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy thử thách theo ID", description = "Lấy thông tin chi tiết của một thử thách")
    public ResponseEntity<ResponseDto<ChallengeDefinitionResponse>> getChallengeById(@PathVariable String id) {
        ChallengeDefinitionResponse challenge = challengeDefinitionService.readChallengeDefinitionById(id);
        return ResponseEntity.ok(ResponseDto.<ChallengeDefinitionResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Lấy thông tin thử thách thành công")
                .data(challenge)
                .build());
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Lấy danh sách thử thách theo loại", description = "Lấy các thử thách được lọc theo loại")
    public ResponseEntity<ResponseDto<List<ChallengeDefinitionResponse>>> getChallengesByType(@PathVariable ChallengeType type) {
        List<ChallengeDefinitionResponse> challenges = challengeDefinitionService.readChallengesByType(type);
        return ResponseEntity.ok(ResponseDto.<List<ChallengeDefinitionResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Lấy danh sách thử thách theo loại thành công")
                .data(challenges)
                .build());
    }

    @GetMapping("/restaurant/{restaurantId}")
    @Operation(summary = "Lấy danh sách thử thách theo nhà hàng", description = "Lấy các thử thách của một nhà hàng cụ thể")
    public ResponseEntity<ResponseDto<List<ChallengeDefinitionResponse>>> getChallengesByRestaurant(@PathVariable String restaurantId) {
        List<ChallengeDefinitionResponse> challenges = challengeDefinitionService.readChallengesByRestaurant(restaurantId);
        return ResponseEntity.ok(ResponseDto.<List<ChallengeDefinitionResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Lấy danh sách thử thách theo nhà hàng thành công")
                .data(challenges)
                .build());
    }


    @PostMapping
    @Operation(summary = "Tạo thử thách mới", description = "Tạo một thử thách mới (chỉ Admin/Partner)")
    public ResponseEntity<ResponseDto<ChallengeDefinitionResponse>> createChallenge(@Valid @RequestBody CreateChallengeDefinitionRequest request) {
        ChallengeDefinitionResponse challenge = challengeDefinitionService.createChallengeDefinition(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseDto.<ChallengeDefinitionResponse>builder()
                .status(HttpStatus.CREATED.value())
                .message("Tạo thử thách mới thành công")
                .data(challenge)
                .build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật thử thách", description = "Cập nhật một thử thách đã tồn tại (chỉ Admin/Partner)")
    public ResponseEntity<ResponseDto<ChallengeDefinitionResponse>> updateChallenge(
            @PathVariable String id, 
            @Valid @RequestBody UpdateChallengeDefinitionRequest request) {
        ChallengeDefinitionResponse challenge = challengeDefinitionService.updateChallengeDefinition(id, request);
        return ResponseEntity.ok(ResponseDto.<ChallengeDefinitionResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Cập nhật thử thách thành công")
                .data(challenge)
                .build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa thử thách", description = "Xóa một thử thách (chỉ Admin/Partner)")
    public ResponseEntity<ResponseDto<Void>> deleteChallenge(@PathVariable String id) {
        challengeDefinitionService.deleteChallengeDefinition(id);
        return ResponseEntity.ok(ResponseDto.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Xóa thử thách thành công")
                .build());
    }

    @PutMapping("/{id}/activate")
    @Operation(summary = "Kích hoạt thử thách", description = "Kích hoạt một thử thách (chỉ Admin/Partner)")
    public ResponseEntity<ResponseDto<Void>> activateChallenge(@PathVariable String id) {
        challengeDefinitionService.activateChallenge(id);
        return ResponseEntity.ok(ResponseDto.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Kích hoạt thử thách thành công")
                .build());
    }

    @PutMapping("/{id}/deactivate")
    @Operation(summary = "Vô hiệu hóa thử thách", description = "Vô hiệu hóa một thử thách (chỉ Admin/Partner)")
    public ResponseEntity<ResponseDto<Void>> deactivateChallenge(@PathVariable String id) {
        challengeDefinitionService.deactivateChallenge(id);
        return ResponseEntity.ok(ResponseDto.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Vô hiệu hóa thử thách thành công")
                .build());
    }
}


