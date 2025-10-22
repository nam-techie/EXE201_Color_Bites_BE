package com.exe201.color_bites_be.dto.response;

import lombok.*;
import java.math.BigDecimal;
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
    private String region;
    private BigDecimal avgPrice;
    private BigDecimal rating;
    private Boolean featured;
    private BigDecimal latitude;
    private BigDecimal longitude;

    // Food types instead of single type
    private List<FoodTypeResponse> foodTypes;

    // Người tạo (tham chiếu đến accounts.id)
    private String createdById;
    private String createdBy;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isDeleted;

    // Các thông tin mở rộng (có thể tính thêm ở service)
    private Boolean isFavorited;
    private Long favoriteCount;
    private Double distance; // khoảng cách từ vị trí user (meters)
}
