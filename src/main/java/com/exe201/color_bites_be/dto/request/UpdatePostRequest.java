package com.exe201.color_bites_be.dto.request;

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
public class UpdatePostRequest {
    
    @Size(max = 200, message = "Tiêu đề không được vượt quá 200 ký tự")
    private String title;
    
    @Size(max = 5000, message = "Nội dung không được vượt quá 5000 ký tự")
    private String content;
    
    @Size(max = 50, message = "Mood không được vượt quá 50 ký tự")
    private String mood;
    
    @Size(max = 10, message = "Không được upload quá 10 hình ảnh")
    private List<String> imageUrls;
    
    private String videoUrl;
    
    @Size(max = 20, message = "Không được thêm quá 20 tag")
    private List<String> tagNames;
}
