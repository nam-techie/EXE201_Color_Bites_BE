package com.exe201.color_bites_be.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request DTO cho toggle reaction
 * Đơn giản như Instagram - chỉ cần postId từ path param
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ToggleReactionRequest {
    
    // Optional: có thể thêm metadata sau này
    private String deviceInfo;
    
    // Optional: source của action (web, mobile, etc.)
    private String source;
}
