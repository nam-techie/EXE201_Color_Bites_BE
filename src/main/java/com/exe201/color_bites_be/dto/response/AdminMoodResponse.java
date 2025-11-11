package com.exe201.color_bites_be.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO response cho admin mood management
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminMoodResponse {
    private String id;
    private String name;
    private String emoji;
    private LocalDateTime createdAt;
}

