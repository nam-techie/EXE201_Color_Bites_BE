package com.exe201.color_bites_be.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateQuizRequest {
    
    @NotNull(message = "Câu trả lời không được để trống")
    @Size(min = 1, message = "Phải có ít nhất 1 câu trả lời")
    private Map<String, Object> answers;
    
    @NotBlank(message = "Kết quả mood không được để trống")
    private String moodResult;
    
    private List<String> recommendedFoods;
    
    private List<String> recommendedRestaurants;
}
