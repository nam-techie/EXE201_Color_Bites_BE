package com.exe201.color_bites_be.service;

import com.exe201.color_bites_be.dto.request.AddFavoriteRequest;
import com.exe201.color_bites_be.dto.response.FavoriteResponse;
import org.springframework.data.domain.Page;

/**
 * Interface định nghĩa các phương thức quản lý yêu thích
 * Bao gồm thêm/xóa yêu thích và lấy danh sách
 */
public interface IFavoriteService {
    
    /**
     * Thêm/xóa yêu thích nhà hàng
     */
    FavoriteResponse toggleFavorite(String accountId, AddFavoriteRequest request);
    
    /**
     * Lấy danh sách nhà hàng yêu thích của người dùng
     */
    Page<FavoriteResponse> readUserFavorites(String accountId, int page, int size);
    
    /**
     * Kiểm tra nhà hàng có được yêu thích không
     */
    boolean isRestaurantFavorited(String accountId, String restaurantId);
    
    /**
     * Đếm số lượng yêu thích của nhà hàng
     */
    long countFavoritesByRestaurant(String restaurantId);
}
