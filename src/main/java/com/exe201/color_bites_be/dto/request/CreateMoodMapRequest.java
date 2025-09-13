package com.exe201.color_bites_be.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateMoodMapRequest {
    
    @NotBlank(message = "Tiêu đề không được để trống")
    @Size(min = 2, max = 100, message = "Tiêu đề phải từ 2-100 ký tự")
    private String title;
    
    @NotNull(message = "Dữ liệu entries không được để trống")
    private Map<String, Object> entries;
    
    @NotBlank(message = "Chế độ hiển thị không được để trống")
    @Pattern(regexp = "^(public|private)$", message = "Chế độ hiển thị phải là 'public' hoặc 'private'")
    private String visibility;
}
