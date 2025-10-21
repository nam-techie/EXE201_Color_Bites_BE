package com.exe201.color_bites_be.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddFavoriteRequest {
    
    @NotBlank(message = "ID nhà hàng không được để trống")
    private String restaurantId;
}
