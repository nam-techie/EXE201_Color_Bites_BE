package com.exe201.color_bites_be.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request DTO để tạo mood mới
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateMoodRequest {
    
    @NotBlank(message = "Tên mood không được để trống")
    @Size(min = 2, max = 50, message = "Tên mood phải từ 2-50 ký tự")
    private String name;
    
    @NotBlank(message = "Emoji không được để trống")
    @Size(min = 1, max = 10, message = "Emoji phải từ 1-10 ký tự")
    private String emoji;
}
