package com.exe201.color_bites_be.service;

import com.exe201.color_bites_be.entity.Tag;
import com.exe201.color_bites_be.exception.DuplicateEntity;
import com.exe201.color_bites_be.exception.NotFoundException;
import com.exe201.color_bites_be.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TagService {
    
    @Autowired
    private TagRepository tagRepository;
    
    /**
     * Tạo tag mới
     */
    @Transactional
    public Tag createTag(String name) {
        // Kiểm tra tag đã tồn tại chưa
        if (tagRepository.existsByName(name.toLowerCase())) {
            throw new DuplicateEntity("Tag này đã tồn tại");
        }
        
        Tag tag = new Tag();
        tag.setName(name.toLowerCase());
        tag.setUsageCount(0);
        tag.setCreatedAt(LocalDateTime.now());
        
        return tagRepository.save(tag);
    }
    
    /**
     * Lấy tag theo ID
     */
    public Tag getTagById(String id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tag không tồn tại"));
    }
    
    /**
     * Lấy tag theo tên
     */
    public Tag getTagByName(String name) {
        return tagRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new NotFoundException("Tag không tồn tại"));
    }
    
    /**
     * Lấy tất cả tags
     */
    public Page<Tag> getAllTags(Pageable pageable) {
        return tagRepository.findAll(pageable);
    }
    
    /**
     * Tìm kiếm tags theo từ khóa
     */
    public Page<Tag> searchTags(String keyword, Pageable pageable) {
        return tagRepository.findByNameContainingIgnoreCase(keyword, pageable);
    }
    
    /**
     * Lấy tags phổ biến
     */
    public Page<Tag> getPopularTags(Pageable pageable) {
        return tagRepository.findAllByOrderByUsageCountDesc(pageable);
    }
    
    /**
     * Lấy tags có usage count > 0
     */
    public Page<Tag> getUsedTags(Pageable pageable) {
        return tagRepository.findByUsageCountGreaterThanOrderByUsageCountDesc(0, pageable);
    }
    
    /**
     * Lấy tags theo danh sách tên
     */
    public List<Tag> getTagsByNames(List<String> names) {
        return tagRepository.findByNameIn(names);
    }
    
    /**
     * Tăng usage count của tag
     */
    @Transactional
    public void incrementUsageCount(String tagId) {
        Tag tag = getTagById(tagId);
        tag.setUsageCount(tag.getUsageCount() + 1);
        tagRepository.save(tag);
    }
    
    /**
     * Giảm usage count của tag
     */
    @Transactional
    public void decrementUsageCount(String tagId) {
        Tag tag = getTagById(tagId);
        if (tag.getUsageCount() > 0) {
            tag.setUsageCount(tag.getUsageCount() - 1);
            tagRepository.save(tag);
        }
    }
    
    /**
     * Cập nhật tag
     */
    @Transactional
    public Tag updateTag(String id, String newName) {
        Tag tag = getTagById(id);
        
        // Kiểm tra tên mới có trùng không
        if (!tag.getName().equals(newName.toLowerCase()) && 
            tagRepository.existsByName(newName.toLowerCase())) {
            throw new DuplicateEntity("Tag này đã tồn tại");
        }
        
        tag.setName(newName.toLowerCase());
        return tagRepository.save(tag);
    }
    
    /**
     * Xóa tag
     */
    @Transactional
    public void deleteTag(String id) {
        Tag tag = getTagById(id);
        
        // Chỉ xóa tag không được sử dụng
        if (tag.getUsageCount() > 0) {
            throw new RuntimeException("Không thể xóa tag đang được sử dụng");
        }
        
        tagRepository.deleteById(id);
    }
    
    /**
     * Tìm hoặc tạo tag
     */
    @Transactional
    public Tag findOrCreateTag(String name) {
        return tagRepository.findByNameIgnoreCase(name)
                .orElseGet(() -> {
                    Tag newTag = new Tag();
                    newTag.setName(name.toLowerCase());
                    newTag.setUsageCount(0);
                    newTag.setCreatedAt(LocalDateTime.now());
                    return tagRepository.save(newTag);
                });
    }
    
    /**
     * Kiểm tra tag có tồn tại không
     */
    public boolean existsByName(String name) {
        return tagRepository.existsByName(name.toLowerCase());
    }
} 