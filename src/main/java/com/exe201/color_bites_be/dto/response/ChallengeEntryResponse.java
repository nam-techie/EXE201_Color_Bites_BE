package com.exe201.color_bites_be.dto.response;

import com.exe201.color_bites_be.enums.EntryStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChallengeEntryResponse {
    
    private String id;
    private String participationId;
    private String restaurantId;
    private String photoUrl;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private EntryStatus status;
    private String notes;
    private LocalDateTime createdAt;
    
    // Additional fields for display
    private String restaurantName;
    private String challengeTitle;
    private String accountId; // User who submitted
}



