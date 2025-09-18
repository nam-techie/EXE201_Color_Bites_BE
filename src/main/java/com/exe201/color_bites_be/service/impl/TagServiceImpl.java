package com.exe201.color_bites_be.service.impl;

import com.exe201.color_bites_be.dto.request.CreateTagRequest;
import com.exe201.color_bites_be.dto.request.UpdateTagRequest;
import com.exe201.color_bites_be.dto.response.TagResponse;
import com.exe201.color_bites_be.entity.Tag;
import com.exe201.color_bites_be.exception.DuplicateEntity;
import com.exe201.color_bites_be.exception.NotFoundException;
import com.exe201.color_bites_be.repository.TagRepository;
import com.exe201.color_bites_be.repository.PostTagRepository;
import com.exe201.color_bites_be.service.ITagService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Implementation của ITagService
 * Xử lý tất cả business logic liên quan đến tag
 */
@Service
@Transactional
public class TagServiceImpl implements ITagService {

    @Autowired
    private TagRepository tagRepository;
    
    @Autowired
    private PostTagRepository postTagRepository;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Tạo tag mới
     */
    @Override
    public TagResponse createTag(CreateTagRequest request) {
        // Chuẩn hóa tên tag
        String normalizedTagName = request.getName().trim().toLowerCase();

        if (tagRepository.existsByNameIgnoreCase(normalizedTagName)) {
            throw new DuplicateEntity("Tag với tên '" + normalizedTagName + "' đã tồn tại");
        }
        
        // Tạo tag mới
        Tag tag = new Tag();
        tag.setName(normalizedTagName);
        tag.setUsageCount(0); // Khởi tạo usage count = 0
        tag.setCreatedAt(LocalDateTime.now());
        
        // Lưu tag
        Tag savedTag = tagRepository.save(tag);
        
        // Trả về response
        return modelMapper.map(savedTag, TagResponse.class);
    }

    /**
     * Lấy thông tin tag theo ID
     */
    @Override
    public TagResponse readTagById(String tagId) {
        // Tìm tag theo ID
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy tag với ID: " + tagId));
        
        // Trả về response
        return modelMapper.map(tag, TagResponse.class);
    }

    /**
     * Lấy danh sách tất cả tag
     */
    @Override
    public Page<TagResponse> readAllTags(int page, int size) {
        // Tạo pageable với sắp xếp theo tên
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        
        // Lấy danh sách tag
        Page<Tag> tags = tagRepository.findAll(pageable);
        
        // Convert sang TagResponse
        return tags.map(tag -> modelMapper.map(tag, TagResponse.class));
    }

    /**
     * Lấy danh sách tag phổ biến nhất
     */
    @Override
    public Page<TagResponse> readPopularTags(int page, int size) {
        // Tạo pageable với sắp xếp theo usage count giảm dần
        Pageable pageable = PageRequest.of(page, size, Sort.by("usageCount").descending());
        
        // Lấy tag phổ biến
        Page<Tag> tags = tagRepository.findMostPopularTags(pageable);
        
        // Convert sang TagResponse
        return tags.map(tag -> modelMapper.map(tag, TagResponse.class));
    }

    /**
     * Tìm kiếm tag theo từ khóa
     */
    @Override
    public Page<TagResponse> searchTags(String keyword, int page, int size) {
        // Tạo pageable với sắp xếp theo usage count giảm dần
        Pageable pageable = PageRequest.of(page, size, Sort.by("usageCount").descending());
        
        // Tìm kiếm tag
        Page<Tag> tags = tagRepository.findByNameContaining(keyword, pageable);
        
        // Convert sang TagResponse
        return tags.map(tag -> modelMapper.map(tag, TagResponse.class));
    }

    /**
     * Cập nhật thông tin tag
     */
    @Override
    public TagResponse editTag(String tagId, UpdateTagRequest request) {
        // Tìm tag cần cập nhật
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy tag với ID: " + tagId));
        
        // Chuẩn hóa tên tag mới
        String normalizedTagName = request.getName().trim().toLowerCase();
        
        // Kiểm tra tên mới có trùng với tag khác không
        if (!tag.getName().equals(normalizedTagName) && tagRepository.existsByNameIgnoreCase(normalizedTagName)) {
            throw new DuplicateEntity("Tag với tên '" + normalizedTagName + "' đã tồn tại");
        }
        
        // Cập nhật thông tin
        tag.setName(normalizedTagName);
        
        // Lưu tag đã cập nhật
        Tag updatedTag = tagRepository.save(tag);
        
        // Trả về response
        return modelMapper.map(updatedTag, TagResponse.class);
    }

    /**
     * Xóa tag
     */
    @Override
    public void deleteTag(String tagId) {
        // Tìm tag cần xóa
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy tag với ID: " + tagId));
        
        // Xóa tất cả liên kết PostTag trước
        postTagRepository.deleteByTagId(tagId);
        
        // Xóa tag
        tagRepository.delete(tag);
    }

    /**
     * Đếm số lượng tag
     */
    @Override
    public long countTags() {
        return tagRepository.count();
    }
}
