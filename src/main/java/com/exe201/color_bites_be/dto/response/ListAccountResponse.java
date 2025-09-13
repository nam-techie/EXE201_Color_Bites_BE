package com.exe201.color_bites_be.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ListAccountResponse {
    private String id;
    private String username;
    private String fullName;
    private boolean isActive;
    private String role;
    private String avatarUrl;
    private LocalDateTime created;
    private LocalDateTime updated;
}
