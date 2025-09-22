package com.exe201.color_bites_be.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateCommentRequest {
    
    @NotBlank(message = "Nội dung comment không được để trống")
    @Size(max = 1000, message = "Nội dung comment không được vượt quá 1000 ký tự")
    private String content;
    

    private String parentCommentId;
}
