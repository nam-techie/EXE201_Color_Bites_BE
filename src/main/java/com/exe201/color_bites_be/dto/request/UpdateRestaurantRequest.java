package com.exe201.color_bites_be.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRestaurantRequest {
    
    @Size(min = 2, max = 100, message = "Tên nhà hàng phải từ 2-100 ký tự")
    private String name;
    
    @Size(max = 200, message = "Địa chỉ không được quá 200 ký tự")
    private String address;
    
    @Size(min = 2, max = 2, message = "Tọa độ phải có đúng 2 giá trị [longitude, latitude]")
    private double[] coordinates; // [longitude, latitude]
    
    @Size(max = 500, message = "Mô tả không được quá 500 ký tự")
    private String description;
    
    private List<String> foodTypeIds;
    
    private String region;
    
    private List<String> imageUrls;
    
    @DecimalMin(value = "0.0", message = "Giá trung bình phải lớn hơn hoặc bằng 0")
    private Double avgPrice;
    
    @DecimalMin(value = "0.0", message = "Đánh giá phải từ 0.0")
    @DecimalMax(value = "5.0", message = "Đánh giá phải nhỏ hơn hoặc bằng 5.0")
    private Double rating;
    
    private Boolean featured;
}
