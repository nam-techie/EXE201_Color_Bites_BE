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
    private String name;              // Tên quán
    private String address;           // Địa chỉ chi tiết
    private String district;          // Quận/huyện
    private String type;              // Loại món (Gà, Bún, v.v.)
    private String price;             // Chuỗi giá "15.000 - 45.000"
    private Double latitude;          // Vĩ độ
    private Double longitude;         // Kinh độ

    // Người tạo (tham chiếu đến accounts.id)
    private String createdById;
    private String createdBy;     // nếu bạn join sang account có thể điền ở service

    private LocalDateTime createdAt;        // ISODate trong Mongo
    private Boolean isDeleted;        // soft delete

//    // Các thông tin mở rộng (không có trong DB nhưng FE có thể tính thêm)
//    private Boolean isFavorited;      // user hiện tại đã yêu thích chưa
//    private Long favoriteCount;       // tổng số người yêu thích
//    private Double distance;          // khoảng cách từ vị trí user
}
