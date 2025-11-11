package com.exe201.color_bites_be.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

/**
 * Entity for type_objects collection
 * Global catalog of food types with key, name, and image URL
 */
@Document(collection = "type_objects")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TypeObjects {

    @Id
    private String id;

    @Field("name")
    @Indexed(unique = true)
    private String name; // e.g. "Korean Food"

    @Field("image_url")
    private String imageUrl;

    @Field("is_active")
    @Indexed
    private Boolean isActive = true;

    @Field("created_at")
    @CreatedDate
    private LocalDateTime createdAt;

    @Field("updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
