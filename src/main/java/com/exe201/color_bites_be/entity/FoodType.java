package com.exe201.color_bites_be.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "food_types")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class FoodType {

    @Id
    @Field("_id")
    private String id;

    @Field("name")
    @Indexed(unique = true)
    private String name;

    @Field("created_at")
    @CreatedDate
    private LocalDateTime createdAt;
}
