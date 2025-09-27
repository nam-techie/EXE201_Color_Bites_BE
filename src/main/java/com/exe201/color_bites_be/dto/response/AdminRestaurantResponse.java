package com.exe201.color_bites_be.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO response cho admin view restaurants
 * Chứa thông tin chi tiết về nhà hàng cho admin quản lý
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminRestaurantResponse {
    
    private String id;
    private String name;
    private String address;
    private Double longitude;
    private Double latitude;
    private String description;
    private String type;
    private String region;
    private Double avgPrice;
    private Double rating;
    private Boolean featured;
    private String createdBy;
    private String createdByName;
    private LocalDateTime createdAt;
    private Boolean isDeleted;
    
    // Thông tin bổ sung cho admin
    private String creatorEmail;
    private Boolean creatorIsActive;
    private String creatorRole;
    private Long favoriteCount;
}
