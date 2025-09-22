package com.exe201.color_bites_be.service;

import com.exe201.color_bites_be.dto.request.CreateMoodRequest;
import com.exe201.color_bites_be.dto.request.UpdateMoodRequest;
import com.exe201.color_bites_be.dto.response.MoodResponse;
import org.springframework.data.domain.Page;

/**
 * Interface định nghĩa các phương thức quản lý mood
 * Bao gồm CRUD, tìm kiếm và thống kê mood
 */
public interface IMoodService {
    
    /**
     * Tạo mood mới
     */
    MoodResponse createMood(CreateMoodRequest request);
    
    /**
     * Lấy mood theo ID
     */
    MoodResponse readMoodById(String moodId);
    
    /**
     * Lấy tất cả mood (có phân trang)
     */
    Page<MoodResponse> readAllMoods(int page, int size);
    
    /**
     * Tìm kiếm mood theo từ khóa
     */
    Page<MoodResponse> searchMoods(String keyword, int page, int size);
    
    /**
     * Cập nhật mood
     */
    MoodResponse editMood(String moodId, UpdateMoodRequest request);
    
    /**
     * Xóa mood
     */
    void deleteMood(String moodId);
    
    /**
     * Đếm tổng số mood
     */
    long countAllMoods();
    
    /**
     * Lấy mood phổ biến (được sử dụng nhiều trong bài viết)
     */
    Page<MoodResponse> readPopularMoods(int page, int size);
    
    /**
     * Kiểm tra mood có tồn tại theo tên
     */
    boolean existsByName(String name);
}
