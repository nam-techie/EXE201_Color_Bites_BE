package com.exe201.color_bites_be.controller;

import com.exe201.color_bites_be.dto.request.CreateMoodRequest;
import com.exe201.color_bites_be.dto.request.UpdateMoodRequest;
import com.exe201.color_bites_be.dto.response.MoodResponse;
import com.exe201.color_bites_be.dto.response.ResponseDto;
import com.exe201.color_bites_be.exception.NotFoundException;
import com.exe201.color_bites_be.exception.FuncErrorException;
import com.exe201.color_bites_be.service.IMoodService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller quản lý mood
 * Cung cấp các endpoint CRUD cho mood
 */
@RestController
@PreAuthorize("hasAuthority('USER')")
@RequestMapping("/api/moods")
public class MoodController {

    @Autowired
    private IMoodService moodService;

    /**
     * Tạo mood mới
     */
    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ADMIN')") // Chỉ admin mới được tạo mood
    public ResponseDto<MoodResponse> createMood(
            @Valid @RequestBody CreateMoodRequest request) {
        try {
            MoodResponse response = moodService.createMood(request);
            return new ResponseDto<>(HttpStatus.CREATED.value(), "Mood đã được tạo thành công", response);
        } catch (FuncErrorException e) {
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi tạo mood: " + e.getMessage(), null);
        }
    }

    /**
     * Lấy mood theo ID
     */
    @GetMapping("/read/{moodId}")
    public ResponseDto<MoodResponse> readMoodById(
            @PathVariable String moodId) {
        try {
            MoodResponse response = moodService.readMoodById(moodId);
            return new ResponseDto<>(HttpStatus.OK.value(), "Thông tin mood đã được tải thành công", response);
        } catch (NotFoundException e) {
            return new ResponseDto<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi lấy thông tin mood", null);
        }
    }

    /**
     * Lấy danh sách tất cả mood (có phân trang)
     */
    @GetMapping("/list")
    public ResponseDto<Page<MoodResponse>> readAllMoods(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<MoodResponse> moods = moodService.readAllMoods(page - 1, size);
            return new ResponseDto<>(HttpStatus.OK.value(), "Danh sách mood đã được tải thành công", moods);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi lấy danh sách mood", null);
        }
    }

    /**
     * Tìm kiếm mood
     */
    @GetMapping("/search")
    public ResponseDto<Page<MoodResponse>> searchMoods(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<MoodResponse> moods = moodService.searchMoods(keyword, page - 1, size);
            return new ResponseDto<>(HttpStatus.OK.value(), "Kết quả tìm kiếm mood đã được tải thành công", moods);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi tìm kiếm mood", null);
        }
    }

    /**
     * Lấy mood phổ biến
     */
    @GetMapping("/read/popular")
    public ResponseDto<Page<MoodResponse>> readPopularMoods(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<MoodResponse> moods = moodService.readPopularMoods(page - 1, size);
            return new ResponseDto<>(HttpStatus.OK.value(), "Danh sách mood phổ biến đã được tải thành công", moods);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi lấy danh sách mood phổ biến", null);
        }
    }

    /**
     * Cập nhật mood
     */
    @PutMapping("/edit/{moodId}")
    @PreAuthorize("hasAuthority('ADMIN')") // Chỉ admin mới được sửa mood
    public ResponseDto<MoodResponse> editMood(
            @PathVariable String moodId,
            @Valid @RequestBody UpdateMoodRequest request) {
        try {
            MoodResponse response = moodService.editMood(moodId, request);
            return new ResponseDto<>(HttpStatus.OK.value(), "Mood đã được cập nhật thành công", response);
        } catch (NotFoundException e) {
            return new ResponseDto<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
        } catch (FuncErrorException e) {
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi cập nhật mood", null);
        }
    }

    /**
     * Xóa mood
     */
    @DeleteMapping("/delete/{moodId}")
    @PreAuthorize("hasAuthority('ADMIN')") // Chỉ admin mới được xóa mood
    public ResponseDto<String> deleteMood(
            @PathVariable String moodId) {
        try {
            moodService.deleteMood(moodId);
            return new ResponseDto<>(HttpStatus.OK.value(), "Mood đã được xóa thành công", null);
        } catch (NotFoundException e) {
            return new ResponseDto<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
        } catch (FuncErrorException e) {
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi xóa mood", null);
        }
    }

    /**
     * Đếm tổng số mood
     */
    @GetMapping("/count")
    public ResponseDto<Long> countAllMoods() {
        try {
            long count = moodService.countAllMoods();
            return new ResponseDto<>(HttpStatus.OK.value(), "Số lượng mood đã được tải thành công", count);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi đếm số lượng mood", null);
        }
    }

    /**
     * Kiểm tra mood có tồn tại theo tên
     */
    @GetMapping("/exists")
    public ResponseDto<Boolean> checkMoodExists(
            @RequestParam String name) {
        try {
            boolean exists = moodService.existsByName(name);
            String message = exists ? "Mood đã tồn tại" : "Mood chưa tồn tại";
            return new ResponseDto<>(HttpStatus.OK.value(), message, exists);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi kiểm tra mood", null);
        }
    }
}
