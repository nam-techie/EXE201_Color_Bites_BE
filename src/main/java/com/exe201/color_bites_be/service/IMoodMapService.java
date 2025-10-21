package com.exe201.color_bites_be.service;

import com.exe201.color_bites_be.dto.request.CreateMoodMapRequest;
import com.exe201.color_bites_be.dto.request.UpdateMoodMapRequest;
import com.exe201.color_bites_be.dto.response.MoodMapResponse;
import org.springframework.data.domain.Page;

/**
 * Interface định nghĩa các phương thức quản lý mood map
 * Bao gồm theo dõi cảm xúc và xuất dữ liệu
 */
public interface IMoodMapService {
    
    /**
     * Tạo mood map mới
     */
    MoodMapResponse createMoodMap(String accountId, CreateMoodMapRequest request);
    
    /**
     * Lấy mood map theo ID
     */
    MoodMapResponse readMoodMapById(String moodMapId, String currentAccountId);
    
    /**
     * Lấy tất cả mood map của người dùng
     */
    Page<MoodMapResponse> readUserMoodMaps(String accountId, int page, int size, String currentAccountId);
    
    /**
     * Lấy mood map công khai
     */
    Page<MoodMapResponse> readPublicMoodMaps(int page, int size, String currentAccountId);
    
    /**
     * Tìm kiếm mood map
     */
    Page<MoodMapResponse> searchMoodMaps(String keyword, int page, int size, String currentAccountId);
    
    /**
     * Cập nhật mood map
     */
    MoodMapResponse editMoodMap(String moodMapId, String accountId, UpdateMoodMapRequest request);
    
    /**
     * Xóa mood map
     */
    void deleteMoodMap(String moodMapId, String accountId);
    
    /**
     * Xuất dữ liệu mood map
     */
    String exportMoodMapData(String moodMapId, String accountId);
}
