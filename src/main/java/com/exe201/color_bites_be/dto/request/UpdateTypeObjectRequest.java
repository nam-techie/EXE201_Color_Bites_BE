package com.exe201.color_bites_be.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Request DTO for updating TypeObject
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTypeObjectRequest {
    
    @NotBlank(message = "Tên loại món ăn không được để trống")
    @Size(min = 2, max = 100, message = "Tên loại món ăn phải từ 2-100 ký tự")
    private String name;
    
    @Size(max = 500, message = "URL hình ảnh không được quá 500 ký tự")
    private String imageUrl;
    
    private Boolean isActive = true;
}
