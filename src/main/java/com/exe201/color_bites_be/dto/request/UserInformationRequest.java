package com.exe201.color_bites_be.dto.request;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserInformationRequest {

    @Pattern(regexp = "^(MALE|FEMALE)$", message = "Giới tính phải là Nam hoặc Nữ")
    private String gender;

    @Size(max = 500, message = "Tiểu sử không được vượt quá 500 ký tự")
    private String bio;
}
