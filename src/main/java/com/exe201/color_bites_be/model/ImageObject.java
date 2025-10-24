package com.exe201.color_bites_be.model;

import lombok.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Model class for ImageObject JSON embedded in Restaurant and ChallengeDefinition entities
 * Represents an image with URL and sort order
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageObject {
    
    @NotBlank(message = "URL hình ảnh không được để trống")
    @Size(max = 500, message = "URL hình ảnh không được quá 500 ký tự")
    private String url;
    
    @NotNull(message = "Thứ tự sắp xếp không được để trống")
    private Integer sortOrder;
}
