package com.exe201.color_bites_be.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReactionRequest {
    
    @NotBlank(message = "Loại reaction không được để trống")
    private String reactionType; // "like", "love", "haha", "wow", "sad", "angry"
} 