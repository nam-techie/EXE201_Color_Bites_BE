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
    private String gender;
    private String avatarUrl;
    private String subscriptionPlan;
    private String subscriptionStatus; // ACTIVE|EXPIRED|CANCELED
    private LocalDateTime subscriptionStartsAt; // ISO timestamps
    private LocalDateTime subscriptionExpiresAt; // ISO timestamps
    private Integer subscriptionRemainingDays; // server-calculated remaining days
    private String bio;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
