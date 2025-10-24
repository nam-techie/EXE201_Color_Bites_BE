package com.exe201.color_bites_be.dto.request;

import com.exe201.color_bites_be.enums.ChallengeType;
import com.exe201.color_bites_be.model.TypeObject;
import com.exe201.color_bites_be.model.ImageObject;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateChallengeDefinitionRequest {
    
    @Size(min = 2, max = 200, message = "Tiêu đề thử thách phải từ 2-200 ký tự")
    private String title;
    
    @Size(max = 1000, message = "Mô tả không được quá 1000 ký tự")
    private String description;
    
    private ChallengeType challengeType;
    
    private String restaurantId; // Required for PARTNER_LOCATION
    
    private TypeObject typeObj; // Required for THEME_COUNT - JSON object embedded
    
    private List<ImageObject> images; // JSON array embedded
    
    @Min(value = 1, message = "Số lượng mục tiêu phải lớn hơn 0")
    private Integer targetCount;
    
    @Future(message = "Ngày bắt đầu phải trong tương lai")
    private LocalDateTime startDate;
    
    private LocalDateTime endDate;
    
    @Size(max = 500, message = "Mô tả phần thưởng không được quá 500 ký tự")
    private String rewardDescription;
    
    private Boolean isActive;
}

