package com.exe201.color_bites_be.service;

import com.exe201.color_bites_be.dto.request.CreateRestaurantRequest;
import com.exe201.color_bites_be.dto.request.UpdateRestaurantRequest;
import com.exe201.color_bites_be.dto.response.RestaurantResponse;
import org.springframework.data.domain.Page;


/**
 * Interface định nghĩa các phương thức quản lý nhà hàng
 * Bao gồm CRUD, tìm kiếm và các tính năng liên quan
 */
public interface IRestaurantService {
    
    /**
     * Tạo nhà hàng mới
     */
    RestaurantResponse createRestaurant(CreateRestaurantRequest request);
    
    /**
     * Lấy thông tin nhà hàng theo ID
     */
    RestaurantResponse readRestaurantById(String restaurantId);
    
    /**
     * Lấy danh sách tất cả nhà hàng
     */
    Page<RestaurantResponse> readAllRestaurants(int page, int size);
    
    /**
     * Tìm kiếm nhà hàng theo từ khóa
     */
    Page<RestaurantResponse> searchRestaurants(String keyword, int page, int size);
    
    /**
     * Lấy nhà hàng theo khu vực
     */
    Page<RestaurantResponse> readRestaurantsByRegion(String region, int page, int size);
    
    /**
     * Lấy nhà hàng theo mood
     */
    Page<RestaurantResponse> readRestaurantsByMood(String mood, int page, int size);
    
    /**
     * Cập nhật thông tin nhà hàng
     */
    RestaurantResponse editRestaurant(String restaurantId, UpdateRestaurantRequest request);
    
    /**
     * Xóa nhà hàng
     */
    void deleteRestaurant(String restaurantId);
}
