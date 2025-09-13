package com.exe201.color_bites_be.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantResponse {
    
    private String id;
    private String name;
    private String address;
    private double[] coordinates; // [longitude, latitude]
    private String description;
    private String type;
    private List<String> moodTags;
    private String region;
    private List<String> imageUrls;
    private Double avgPrice;
    private Double rating;
    private Boolean featured;
    private String createdById;
    private String createdByName;
    private LocalDateTime createdAt;
    
    // Thêm thông tin bổ sung cho response
    private Boolean isFavorited; // Người dùng hiện tại đã yêu thích chưa
    private Long favoriteCount; // Số lượng người yêu thích
    private Double distance; // Khoảng cách từ vị trí hiện tại (nếu có)
}
