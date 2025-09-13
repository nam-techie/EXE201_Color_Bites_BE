package com.exe201.color_bites_be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserInformationResponse {
    private String username;
    private String accountId;
    private String fullName;
    private String gender;
    private LocalDate dob;
    private String phone;
    private String address;
    private String avatarUrl;
    private String subscriptionPlan;
    private String bio;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
