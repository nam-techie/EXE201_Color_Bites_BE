package com.exe201.color_bites_be.controller;

import com.exe201.color_bites_be.dto.request.JoinChallengeRequest;
import com.exe201.color_bites_be.dto.response.ChallengeParticipationResponse;
import com.exe201.color_bites_be.dto.response.ChallengeProgressResponse;
import com.exe201.color_bites_be.dto.response.ResponseDto;
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
@Tag(name = "ChallengeParticipationController", description = "API quản lý việc tham gia các thử thách")
public class ChallengeParticipationController {

    @Autowired
    private IChallengeParticipationService participationService;

    @PostMapping("/{challengeId}/join")
    @Operation(summary = "Tham gia thử thách", description = "Tham gia vào một thử thách")
    public ResponseEntity<ResponseDto<ChallengeParticipationResponse>> joinChallenge(
            @PathVariable String challengeId, 
            @Valid @RequestBody JoinChallengeRequest request) {
        ChallengeParticipationResponse participation = participationService.joinChallenge(challengeId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseDto.<ChallengeParticipationResponse>builder()
                .status(HttpStatus.CREATED.value())
                .message("Tham gia thử thách thành công")
                .data(participation)
                .build());
    }

    @GetMapping("/participations/{participationId}")
    @Operation(summary = "Lấy thông tin tham gia theo ID", description = "Lấy thông tin chi tiết của một lần tham gia thử thách")
    public ResponseEntity<ResponseDto<ChallengeParticipationResponse>> getParticipationById(@PathVariable String participationId) {
        ChallengeParticipationResponse participation = participationService.readParticipationById(participationId);
        return ResponseEntity.ok(ResponseDto.<ChallengeParticipationResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Lấy thông tin tham gia thành công")
                .data(participation)
                .build());
    }

    @GetMapping("/my-participations")
    @Operation(summary = "Lấy danh sách tham gia của tôi", description = "Lấy tất cả các lần tham gia thử thách của người dùng hiện tại")
    public ResponseEntity<ResponseDto<List<ChallengeParticipationResponse>>> getMyParticipations() {
        // TODO: Lấy ID người dùng hiện tại từ security context
        String currentUserId = "current-user-id"; // Giá trị tạm thời
        List<ChallengeParticipationResponse> participations = participationService.readUserParticipations(currentUserId);
        return ResponseEntity.ok(ResponseDto.<List<ChallengeParticipationResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Lấy danh sách tham gia của tôi thành công")
                .data(participations)
                .build());
    }

    @GetMapping("/my-participations/paged")
    @Operation(summary = "Lấy danh sách tham gia của tôi (có phân trang)", description = "Lấy danh sách tham gia thử thách có phân trang cho người dùng hiện tại")
    public ResponseEntity<ResponseDto<Page<ChallengeParticipationResponse>>> getMyParticipationsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        // TODO: Lấy ID người dùng hiện tại từ security context
        String currentUserId = "current-user-id"; // Giá trị tạm thời
        Page<ChallengeParticipationResponse> participations = participationService.readUserParticipations(currentUserId, page, size);
        return ResponseEntity.ok(ResponseDto.<Page<ChallengeParticipationResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Lấy danh sách tham gia có phân trang thành công")
                .data(participations)
                .build());
    }

    @GetMapping("/{challengeId}/participations")
    @Operation(summary = "Lấy danh sách tham gia theo thử thách", description = "Lấy tất cả các lần tham gia của một thử thách cụ thể")
    public ResponseEntity<ResponseDto<List<ChallengeParticipationResponse>>> getParticipationsByChallenge(@PathVariable String challengeId) {
        List<ChallengeParticipationResponse> participations = participationService.readParticipationsByChallenge(challengeId);
        return ResponseEntity.ok(ResponseDto.<List<ChallengeParticipationResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Lấy danh sách tham gia theo thử thách thành công")
                .data(participations)
                .build());
    }

    @GetMapping("/participations/status/{status}")
    @Operation(summary = "Lấy danh sách tham gia theo trạng thái", description = "Lấy tất cả các lần tham gia được lọc theo trạng thái")
    public ResponseEntity<ResponseDto<List<ChallengeParticipationResponse>>> getParticipationsByStatus(@PathVariable ParticipationStatus status) {
        List<ChallengeParticipationResponse> participations = participationService.readParticipationsByStatus(status);
        return ResponseEntity.ok(ResponseDto.<List<ChallengeParticipationResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Lấy danh sách tham gia theo trạng thái thành công")
                .data(participations)
                .build());
    }

    @GetMapping("/participations/{participationId}/progress")
    @Operation(summary = "Lấy tiến độ tham gia", description = "Lấy chi tiết tiến độ của một lần tham gia thử thách")
    public ResponseEntity<ResponseDto<ChallengeProgressResponse>> getParticipationProgress(@PathVariable String participationId) {
        ChallengeProgressResponse progress = participationService.getParticipationProgress(participationId);
        return ResponseEntity.ok(ResponseDto.<ChallengeProgressResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Lấy tiến độ tham gia thành công")
                .data(progress)
                .build());
    }

    @PutMapping("/participations/{participationId}/progress")
    @Operation(summary = "Cập nhật tiến độ tham gia", description = "Cập nhật số lượng tiến độ cho một lần tham gia")
    public ResponseEntity<ResponseDto<Void>> updateParticipationProgress(
            @PathVariable String participationId, 
            @RequestParam int progressCount) {
        participationService.updateParticipationProgress(participationId, progressCount);
        return ResponseEntity.ok(ResponseDto.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Cập nhật tiến độ tham gia thành công")
                .build());
    }

    @PutMapping("/participations/{participationId}/complete")
    @Operation(summary = "Hoàn thành tham gia", description = "Đánh dấu một lần tham gia là đã hoàn thành")
    public ResponseEntity<ResponseDto<Void>> completeParticipation(@PathVariable String participationId) {
        participationService.completeParticipation(participationId);
        return ResponseEntity.ok(ResponseDto.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Hoàn thành tham gia thành công")
                .build());
    }

    @PutMapping("/participations/{participationId}/fail")
    @Operation(summary = "Thất bại tham gia", description = "Đánh dấu một lần tham gia là thất bại")
    public ResponseEntity<ResponseDto<Void>> failParticipation(@PathVariable String participationId) {
        participationService.failParticipation(participationId);
        return ResponseEntity.ok(ResponseDto.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Đánh dấu thất bại tham gia thành công")
                .build());
    }
}


