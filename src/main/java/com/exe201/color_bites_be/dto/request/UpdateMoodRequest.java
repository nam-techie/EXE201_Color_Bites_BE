package com.exe201.color_bites_be.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request DTO để cập nhật mood
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMoodRequest {
    
    @Size(min = 2, max = 50, message = "Tên mood phải từ 2-50 ký tự")
    private String name;
    
    @Size(min = 1, max = 10, message = "Emoji phải từ 1-10 ký tự")
    private String emoji;
}
