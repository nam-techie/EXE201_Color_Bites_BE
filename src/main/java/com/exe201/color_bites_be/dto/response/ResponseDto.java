package com.exe201.color_bites_be.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseDto<T> {

    private int status;

    private String message;

    private T data;
    
    // Static factory methods
    public static <T> ResponseDto<T> success(T data, String message) {
        return ResponseDto.<T>builder()
                .status(200)
                .message(message)
                .data(data)
                .build();
    }
    
    public static <T> ResponseDto<T> error(String message) {
        return ResponseDto.<T>builder()
                .status(500)
                .message(message)
                .data(null)
                .build();
    }
}
