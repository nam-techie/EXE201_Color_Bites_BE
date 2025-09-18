package com.exe201.color_bites_be.service;

import com.exe201.color_bites_be.dto.request.CreateTagRequest;
import com.exe201.color_bites_be.dto.request.UpdateTagRequest;
import com.exe201.color_bites_be.dto.response.TagResponse;
import org.springframework.data.domain.Page;

/**
 * Interface định nghĩa các phương thức quản lý tag
 * Bao gồm CRUD, tìm kiếm và các tính năng liên quan
 */
public interface ITagService {
    
    /**
     * Tạo tag mới
     * @param request Thông tin tag cần tạo
     * @return TagResponse Tag đã được tạo
     */
    TagResponse createTag(CreateTagRequest request);
    
    /**
     * Lấy thông tin tag theo ID
     * @param tagId ID của tag
     * @return TagResponse Thông tin tag
     */
    TagResponse readTagById(String tagId);
    
    /**
     * Lấy danh sách tất cả tag
     * @param page Số trang
     * @param size Kích thước trang
     * @return Page<TagResponse> Danh sách tag
     */
    Page<TagResponse> readAllTags(int page, int size);
    
    /**
     * Lấy danh sách tag phổ biến nhất
     * @param page Số trang
     * @param size Kích thước trang
     * @return Page<TagResponse> Danh sách tag phổ biến
     */
    Page<TagResponse> readPopularTags(int page, int size);
    
    /**
     * Tìm kiếm tag theo từ khóa
     * @param keyword Từ khóa tìm kiếm
     * @param page Số trang
     * @param size Kích thước trang
     * @return Page<TagResponse> Kết quả tìm kiếm
     */
    Page<TagResponse> searchTags(String keyword, int page, int size);
    
    /**
     * Cập nhật thông tin tag
     * @param tagId ID của tag
     * @param request Thông tin cập nhật
     * @return TagResponse Tag đã được cập nhật
     */
    TagResponse editTag(String tagId, UpdateTagRequest request);
    
    /**
     * Xóa tag
     * @param tagId ID của tag cần xóa
     */
    void deleteTag(String tagId);
    
    /**
     * Đếm số lượng tag
     * @return long Tổng số tag
     */
    long countTags();
}
