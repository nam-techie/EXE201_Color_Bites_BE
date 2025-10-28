package com.exe201.color_bites_be.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubmitChallengeEntryRequest {
    
    @NotBlank(message = "ID nhà hàng không được để trống")
    private String restaurantId;

    @NotNull(message = "Vĩ độ không được để trống")
    @DecimalMin(value = "-90.0", message = "Vĩ độ phải từ -90 đến 90")
    @DecimalMax(value = "90.0", message = "Vĩ độ phải từ -90 đến 90")
    private BigDecimal latitude;
    
    @NotNull(message = "Kinh độ không được để trống")
    @DecimalMin(value = "-180.0", message = "Kinh độ phải từ -180 đến 180")
    @DecimalMax(value = "180.0", message = "Kinh độ phải từ -180 đến 180")
    private BigDecimal longitude;
    
    @Size(max = 500, message = "Caption không được quá 500 ký tự")
    private String caption;
}






