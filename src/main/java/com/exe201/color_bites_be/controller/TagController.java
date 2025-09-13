package com.exe201.color_bites_be.controller;

import com.exe201.color_bites_be.dto.response.ResponseDto;
import com.exe201.color_bites_be.dto.response.TagResponse;
import com.exe201.color_bites_be.entity.Tag;
import com.exe201.color_bites_be.repository.TagRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@PreAuthorize("hasAuthority('USER')")
@RequestMapping("/api/tags")
public class TagController {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Lấy danh sách tags phổ biến nhất
     */
    @GetMapping("/read/popular")
    public ResponseDto<Page<TagResponse>> readPopularTags(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("usageCount").descending());
            Page<Tag> tags = tagRepository.findMostPopularTags(pageable);

            Page<TagResponse> tagResponses = tags.map(tag -> modelMapper.map(tag, TagResponse.class));
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
            Pageable pageable = PageRequest.of(page, size, Sort.by("usageCount").descending());
            Page<Tag> tags = tagRepository.findByNameContaining(keyword, pageable);

            Page<TagResponse> tagResponses = tags.map(tag -> modelMapper.map(tag, TagResponse.class));
            return new ResponseDto<>(HttpStatus.OK.value(), "Kết quả tìm kiếm tags đã được tải thành công", tagResponses);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi tìm kiếm tags", null);
        }
    }

    /**
     * Lấy tất cả tags
     */
    @GetMapping("/read/all")
    public ResponseDto<Page<TagResponse>> readAllTags(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {

        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
            Page<Tag> tags = tagRepository.findAll(pageable);

            Page<TagResponse> tagResponses = tags.map(tag -> modelMapper.map(tag, TagResponse.class));
            return new ResponseDto<>(HttpStatus.OK.value(), "Danh sách tất cả tags đã được tải thành công", tagResponses);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi lấy danh sách tags", null);
        }
    }
}
