package com.exe201.color_bites_be.dto.request;

import lombok.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TypeObjectRequest {

    @NotBlank(message = "Tên loại món ăn không được để trống")
    @Size(max = 100, message = "Tên loại món ăn không được quá 100 ký tự")
    private String name; // e.g. "Korean Food"
    
    @Size(max = 500, message = "URL hình ảnh không được quá 500 ký tự")
    private String imageUrl; // e.g. "https://..."
}
