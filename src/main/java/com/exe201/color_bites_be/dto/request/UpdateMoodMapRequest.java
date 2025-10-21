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
public class UpdateMoodMapRequest {
    
    @Size(min = 2, max = 100, message = "Tiêu đề phải từ 2-100 ký tự")
    private String title;
    
    private Map<String, Object> entries;
    
    @Pattern(regexp = "^(public|private)$", message = "Chế độ hiển thị phải là 'public' hoặc 'private'")
    private String visibility;
}
