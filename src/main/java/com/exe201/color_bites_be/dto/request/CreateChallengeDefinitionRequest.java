package com.exe201.color_bites_be.dto.request;

import com.exe201.color_bites_be.enums.ChallengeType;
import com.exe201.color_bites_be.model.TypeObject;
import com.exe201.color_bites_be.model.ImageObject;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateChallengeDefinitionRequest {
    
    @NotBlank(message = "Tiêu đề thử thách không được để trống")
    @Size(min = 2, max = 200, message = "Tiêu đề thử thách phải từ 2-200 ký tự")
    private String title;
    
    @Size(max = 1000, message = "Mô tả không được quá 1000 ký tự")
    private String description;
    
    @NotNull(message = "Loại thử thách không được để trống")
    private ChallengeType challengeType;
    
    private String restaurantId; // Required for PARTNER_LOCATION
    
    private TypeObject typeObj; // Required for THEME_COUNT - JSON object embedded
    
    private List<ImageObject> images; // JSON array embedded
    
    @NotNull(message = "Số lượng mục tiêu không được để trống")
    @Min(value = 1, message = "Số lượng mục tiêu phải lớn hơn 0")
    private Integer targetCount;
    
    @NotNull(message = "Ngày bắt đầu không được để trống")
    @Future(message = "Ngày bắt đầu phải trong tương lai")
    private LocalDateTime startDate;
    
    @NotNull(message = "Ngày kết thúc không được để trống")
    private LocalDateTime endDate;
    
    @Size(max = 500, message = "Mô tả phần thưởng không được quá 500 ký tự")
    private String rewardDescription;
    
    // Validation: if PARTNER_LOCATION, restaurantId is required
    // Validation: if THEME_COUNT, foodTypeId is required
    // Validation: endDate must be after startDate
}

