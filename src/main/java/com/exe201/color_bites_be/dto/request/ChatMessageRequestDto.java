package com.exe201.color_bites_be.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatMessageRequestDto {

    @NotNull(message = "Vui lòng nhập đầy đủ thông tin")
    @Min(value = 1, message = "Vui lòng nhập đầy đủ thông tin")
    Integer senderId;

    @NotBlank(message = "Tin nhắn trống")
    String message;
}