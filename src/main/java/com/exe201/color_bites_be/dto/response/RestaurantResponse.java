package com.exe201.color_bites_be.dto.response;

import lombok.*;
import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantResponse {

    private String id;                // _id trong Mongo
    private String name;              // Tên quán (được normalize từ Restaurant Name hoặc name)
    private String address;           // Địa chỉ chi tiết (từ Address hoặc address)
    private String district;          // Quận/huyện (từ quận hoặc district)
    private String type;              // Loại món (từ Type hoặc type: Gà, Bún, v.v.)
    private String price;             // Chuỗi giá "15.000 - 45.000" (từ Price hoặc price)
    private Double latitude;          // Vĩ độ (từ lat hoặc latitude)
    private Double longitude;         // Kinh độ (từ lon hoặc longitude)

    // Người tạo (tham chiếu đến accounts.id)
    private String createdById;
    private String createdBy;         // nếu bạn join sang account có thể điền ở service

    private LocalDateTime createdAt;  // ISODate trong Mongo
    private Boolean isDeleted;        // soft delete

    // Các thông tin mở rộng (có thể tính thêm ở service)
    private Boolean isFavorited;      // user hiện tại đã yêu thích chưa
    private Long favoriteCount;       // tổng số người yêu thích
    private Double distance;          // khoảng cách từ vị trí user (meters)
}
