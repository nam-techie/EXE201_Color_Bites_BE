package com.exe201.color_bites_be.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuizResponse {
    
    private String id;
    private String accountId;
    private String accountName;
    private Map<String, Object> answers;
    private String moodResult;
    private List<String> recommendedFoods;
    private List<String> recommendedRestaurants;
    private List<RestaurantResponse> recommendedRestaurantDetails; // Chi tiết nhà hàng được gợi ý
    private LocalDateTime createdAt;
}
