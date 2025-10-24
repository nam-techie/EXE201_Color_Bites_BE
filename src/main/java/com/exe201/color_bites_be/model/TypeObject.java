package com.exe201.color_bites_be.model;

import lombok.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Model class for TypeObject JSON embedded in Restaurant and ChallengeDefinition entities
 * Represents a food type with key, name, and image URL
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TypeObject {
    
    @NotBlank(message = "Key không được để trống")
    @Size(max = 50, message = "Key không được quá 50 ký tự")
    private String key; // e.g. "KOREAN"
    
    @NotBlank(message = "Tên loại món ăn không được để trống")
    @Size(max = 100, message = "Tên loại món ăn không được quá 100 ký tự")
    private String name; // e.g. "Korean Food"
    
    @Size(max = 500, message = "URL hình ảnh không được quá 500 ký tự")
    private String imageUrl; // e.g. "https://..."
}
