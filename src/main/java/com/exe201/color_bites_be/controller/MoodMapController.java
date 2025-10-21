package com.exe201.color_bites_be.controller;

import com.exe201.color_bites_be.dto.request.CreateMoodMapRequest;
import com.exe201.color_bites_be.dto.request.UpdateMoodMapRequest;
import com.exe201.color_bites_be.dto.response.MoodMapResponse;
import com.exe201.color_bites_be.dto.response.ResponseDto;
import com.exe201.color_bites_be.exception.NotFoundException;
import com.exe201.color_bites_be.exception.FuncErrorException;
import com.exe201.color_bites_be.model.UserPrincipal;
import com.exe201.color_bites_be.service.IMoodMapService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@PreAuthorize("hasAuthority('USER')")
@RequestMapping("/api/moodmaps")
public class MoodMapController {

    @Autowired
    private IMoodMapService moodMapService;

    /**
     * Tạo mood map mới
     */
    @PostMapping("/create")
    public ResponseDto<MoodMapResponse> createMoodMap(
            @Valid @RequestBody CreateMoodMapRequest request,
            BindingResult bindingResult,
            Authentication authentication) {

        if (bindingResult.hasErrors()) {
            StringBuilder errors = new StringBuilder();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.append(error.getDefaultMessage()).append(". ");
            }
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), "Validation failed", null);
        }

        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            String accountId = userPrincipal.getAccount().getId();

            MoodMapResponse response = moodMapService.createMoodMap(accountId, request);
            return new ResponseDto<>(HttpStatus.CREATED.value(), "Mood map đã được tạo thành công", response);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi tạo mood map: " + e.getMessage(), null);
        }
    }

    /**
     * Lấy mood map theo ID
     */
    @GetMapping("/read/{moodMapId}")
    public ResponseDto<MoodMapResponse> readMoodMapById(
            @PathVariable String moodMapId,
            Authentication authentication) {

        try {
            String currentAccountId = null;
            if (authentication != null) {
                UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
                currentAccountId = userPrincipal.getAccount().getId();
            }

            MoodMapResponse response = moodMapService.readMoodMapById(moodMapId, currentAccountId);
            return new ResponseDto<>(HttpStatus.OK.value(), "Thông tin mood map đã được tải thành công", response);
        } catch (NotFoundException e) {
            return new ResponseDto<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
        } catch (FuncErrorException e) {
            return new ResponseDto<>(HttpStatus.FORBIDDEN.value(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi lấy thông tin mood map", null);
        }
    }

    /**
     * Lấy danh sách mood map của user hiện tại
     */
    @GetMapping("/list")
    public ResponseDto<Page<MoodMapResponse>> readUserMoodMaps(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {

        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            String accountId = userPrincipal.getAccount().getId();

            Page<MoodMapResponse> moodMaps = moodMapService.readUserMoodMaps(accountId, page, size, accountId);
            return new ResponseDto<>(HttpStatus.OK.value(), "Danh sách mood map đã được tải thành công", moodMaps);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi lấy danh sách mood map", null);
        }
    }

    /**
     * Lấy mood map của user khác
     */
    @GetMapping("/read/user/{accountId}")
    public ResponseDto<Page<MoodMapResponse>> readMoodMapsByUser(
            @PathVariable String accountId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {

        try {
            String currentAccountId = null;
            if (authentication != null) {
                UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
                currentAccountId = userPrincipal.getAccount().getId();
            }

            Page<MoodMapResponse> moodMaps = moodMapService.readUserMoodMaps(accountId, page, size, currentAccountId);
            return new ResponseDto<>(HttpStatus.OK.value(), "Mood map của người dùng đã được tải thành công", moodMaps);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi lấy mood map của người dùng", null);
        }
    }

    /**
     * Lấy mood map public
     */
    @GetMapping("/public")
    public ResponseDto<Page<MoodMapResponse>> readPublicMoodMaps(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {

        try {
            String currentAccountId = null;
            if (authentication != null) {
                UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
                currentAccountId = userPrincipal.getAccount().getId();
            }

            Page<MoodMapResponse> moodMaps = moodMapService.readPublicMoodMaps(page, size, currentAccountId);
            return new ResponseDto<>(HttpStatus.OK.value(), "Mood map công khai đã được tải thành công", moodMaps);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi lấy mood map công khai", null);
        }
    }

    /**
     * Tìm kiếm mood map
     */
    @GetMapping("/search")
    public ResponseDto<Page<MoodMapResponse>> searchMoodMaps(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {

        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            String accountId = userPrincipal.getAccount().getId();

            Page<MoodMapResponse> moodMaps = moodMapService.searchMoodMaps(keyword, page, size, accountId);
            return new ResponseDto<>(HttpStatus.OK.value(), "Kết quả tìm kiếm mood map đã được tải thành công", moodMaps);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi tìm kiếm mood map", null);
        }
    }

    /**
     * Cập nhật mood map
     */
    @PutMapping("/edit/{moodMapId}")
    public ResponseDto<MoodMapResponse> editMoodMap(
            @PathVariable String moodMapId,
            @Valid @RequestBody UpdateMoodMapRequest request,
            BindingResult bindingResult,
            Authentication authentication) {

        if (bindingResult.hasErrors()) {
            StringBuilder errors = new StringBuilder();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.append(error.getDefaultMessage()).append(". ");
            }
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), "Validation failed", null);
        }

        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            String accountId = userPrincipal.getAccount().getId();

            MoodMapResponse response = moodMapService.editMoodMap(moodMapId, accountId, request);
            return new ResponseDto<>(HttpStatus.OK.value(), "Mood map đã được cập nhật thành công", response);
        } catch (NotFoundException e) {
            return new ResponseDto<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
        } catch (FuncErrorException e) {
            return new ResponseDto<>(HttpStatus.FORBIDDEN.value(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi cập nhật mood map: " + e.getMessage(), null);
        }
    }

    /**
     * Xóa mood map
     */
    @DeleteMapping("/delete/{moodMapId}")
    public ResponseDto<Void> deleteMoodMap(
            @PathVariable String moodMapId,
            Authentication authentication) {

        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            String accountId = userPrincipal.getAccount().getId();

            moodMapService.deleteMoodMap(moodMapId, accountId);
            return new ResponseDto<>(HttpStatus.OK.value(), "Mood map đã được xóa thành công", null);
        } catch (NotFoundException e) {
            return new ResponseDto<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
        } catch (FuncErrorException e) {
            return new ResponseDto<>(HttpStatus.FORBIDDEN.value(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi xóa mood map: " + e.getMessage(), null);
        }
    }

    /**
     * Export mood map
     */
    @PostMapping("/export/{moodMapId}")
    public ResponseDto<MoodMapResponse> exportMoodMap(
            @PathVariable String moodMapId,
            Authentication authentication) {

        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            String accountId = userPrincipal.getAccount().getId();

            String exportData = moodMapService.exportMoodMapData(moodMapId, accountId);
            // Create a simple response with export data
            MoodMapResponse response = new MoodMapResponse();
            // TODO: Set export data properly
            return new ResponseDto<>(HttpStatus.OK.value(), "Mood map đã được export thành công", response);
        } catch (NotFoundException e) {
            return new ResponseDto<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
        } catch (FuncErrorException e) {
            return new ResponseDto<>(HttpStatus.FORBIDDEN.value(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi export mood map: " + e.getMessage(), null);
        }
    }

    /**
     * Đếm số mood map của user hiện tại
     */
    @GetMapping("/count")
    public ResponseDto<Map<String, Object>> countUserMoodMaps(Authentication authentication) {

        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            String accountId = userPrincipal.getAccount().getId();

            // TODO: implement countMoodMapsByUser method
            long moodMapCount = 0;

            Map<String, Object> result = Map.of("moodMapCount", moodMapCount);

            return new ResponseDto<>(HttpStatus.OK.value(), "Số lượng mood map đã được đếm", result);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi đếm số lượng mood map", null);
        }
    }
}
