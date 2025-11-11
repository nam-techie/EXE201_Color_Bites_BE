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
public class CreateRestaurantRequest {
    
    @NotBlank(message = "Tên nhà hàng không được để trống")
    @Size(min = 2, max = 100, message = "Tên nhà hàng phải từ 2-100 ký tự")
    private String name;
    
    @NotBlank(message = "Địa chỉ không được để trống")
    @Size(max = 200, message = "Địa chỉ không được quá 200 ký tự")
    private String address;
    
    @NotNull(message = "Tọa độ không được để trống")
    @Size(min = 2, max = 2, message = "Tọa độ phải có đúng 2 giá trị [longitude, latitude]")
    private double[] coordinates; // [longitude, latitude]
    
    @Size(max = 500, message = "Mô tả không được quá 500 ký tự")
    private String description;
    
    @NotEmpty(message = "Phải chọn ít nhất một loại món ăn")
    private List<TypeObjectRequest> types; // Changed from foodTypeIds to types
    
    @NotBlank(message = "Khu vực không được để trống")
    private String district; // Changed from region to district

    private List<ImageObjectRequest> images; // Changed from imageUrls to images
    
    @NotBlank(message = "Giá không được để trống")
    @Size(max = 100, message = "Giá không được quá 100 ký tự")
    private String price; // Changed from avgPrice (Double) to price (String)
    
    @DecimalMin(value = "0.0", message = "Đánh giá phải từ 0.0")
    @DecimalMax(value = "5.0", message = "Đánh giá phải nhỏ hơn hoặc bằng 5.0")
    private Double rating;
    
    private Boolean featured = false;
}
