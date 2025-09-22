package com.exe201.color_bites_be.controller;

import com.exe201.color_bites_be.dto.request.CreateTagRequest;
import com.exe201.color_bites_be.dto.request.UpdateTagRequest;
import com.exe201.color_bites_be.dto.response.ResponseDto;
import com.exe201.color_bites_be.dto.response.TagResponse;
import com.exe201.color_bites_be.service.ITagService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize("hasAuthority('USER')")
@RequestMapping("/api/tags")
public class TagController {

    @Autowired
    private ITagService tagService;

    /**
     * Tạo tag mới
     */
    @PostMapping("/create")
    public ResponseDto<TagResponse> createTag(@Valid @RequestBody CreateTagRequest request) {
        try {
            TagResponse tagResponse = tagService.createTag(request);
            return new ResponseDto<>(HttpStatus.CREATED.value(), "Tag đã được tạo thành công", tagResponse);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
        }
    }

    /**
     * Lấy thông tin tag theo ID
     */
    @GetMapping("/read/{tagId}")
    public ResponseDto<TagResponse> readTagById(@PathVariable String tagId) {
        try {
            TagResponse tagResponse = tagService.readTagById(tagId);
            return new ResponseDto<>(HttpStatus.OK.value(), "Thông tin tag đã được tải thành công", tagResponse);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
        }
    }

    /**
     * Lấy danh sách tất cả tags
     */
    @GetMapping("/read/all")
    public ResponseDto<Page<TagResponse>> readAllTags(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        try {
            Page<TagResponse> tagResponses = tagService.readAllTags(page, size);
            return new ResponseDto<>(HttpStatus.OK.value(), "Danh sách tất cả tags đã được tải thành công", tagResponses);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi lấy danh sách tags", null);
        }
    }

    /**
     * Lấy danh sách tags phổ biến nhất
     */
    @GetMapping("/read/popular")
    public ResponseDto<Page<TagResponse>> readPopularTags(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Page<TagResponse> tagResponses = tagService.readPopularTags(page, size);
            return new ResponseDto<>(HttpStatus.OK.value(), "Danh sách tags phổ biến đã được tải thành công", tagResponses);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi lấy danh sách tags phổ biến", null);
        }
    }

    /**
     * Tìm kiếm tags
     */
    @GetMapping("/search")
    public ResponseDto<Page<TagResponse>> searchTags(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Page<TagResponse> tagResponses = tagService.searchTags(keyword, page, size);
            return new ResponseDto<>(HttpStatus.OK.value(), "Kết quả tìm kiếm tags đã được tải thành công", tagResponses);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi tìm kiếm tags", null);
        }
    }

    /**
     * Cập nhật thông tin tag
     */
    @PutMapping("/edit/{tagId}")
    public ResponseDto<TagResponse> editTag(
            @PathVariable String tagId, 
            @Valid @RequestBody UpdateTagRequest request) {
        try {
            TagResponse tagResponse = tagService.editTag(tagId, request);
            return new ResponseDto<>(HttpStatus.OK.value(), "Tag đã được cập nhật thành công", tagResponse);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
        }
    }

    /**
     * Xóa tag
     */
    @DeleteMapping("/delete/{tagId}")
    public ResponseDto<Void> deleteTag(@PathVariable String tagId) {
        try {
            tagService.deleteTag(tagId);
            return new ResponseDto<>(HttpStatus.OK.value(), "Tag đã được xóa thành công", null);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
        }
    }

    /**
     * Đếm tổng số tag
     */
    @GetMapping("/count")
    public ResponseDto<Long> countTags() {
        try {
            long count = tagService.countTags();
            return new ResponseDto<>(HttpStatus.OK.value(), "Số lượng tag đã được tải thành công", count);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi đếm số lượng tag", null);
        }
    }
}
