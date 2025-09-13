package com.exe201.color_bites_be.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FavoriteResponse {
    
    private String id;
    private String accountId;
    private String accountName;
    private RestaurantResponse restaurant;
    private LocalDateTime createdAt;
}
