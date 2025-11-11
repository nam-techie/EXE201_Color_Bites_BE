package com.exe201.color_bites_be.dto.response;

import com.exe201.color_bites_be.enums.ChallengeType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChallengeDetailResponse {
    private String id;
    private String title;
    private ChallengeType challengeType;
    private String restaurantId;
    private String typeObjId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
