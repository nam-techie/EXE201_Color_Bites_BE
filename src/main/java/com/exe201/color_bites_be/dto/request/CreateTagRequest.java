package com.exe201.color_bites_be.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request DTO để tạo tag mới
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateTagRequest {
    
    @NotBlank(message = "Tên tag không được để trống")
    @Size(min = 1, max = 50, message = "Tên tag phải từ 1-50 ký tự")
    private String name;
}
