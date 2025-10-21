package com.exe201.color_bites_be.dto.request;

import com.exe201.color_bites_be.enums.Visibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreatePostRequest {
    
    @NotBlank(message = "Nội dung không được để trống")
    @Size(max = 5000, message = "Nội dung không được vượt quá 5000 ký tự")
    private String content;
    
    @Size(max = 50, message = "Mood không được vượt quá 50 ký tự")
    private String moodId;

    private Visibility visibility;
    
//    @Size(max = 20, message = "Không được thêm quá 20 tag")
//    private List<String> tagNames;
}
