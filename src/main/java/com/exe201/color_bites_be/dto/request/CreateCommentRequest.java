package com.exe201.color_bites_be.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCommentRequest {
    
    @NotBlank(message = "Nội dung bình luận không được để trống")
    @Size(max = 1000, message = "Nội dung bình luận không được quá 1000 ký tự")
    private String content;
    
    private String parentCommentId; // null nếu là comment gốc
} 