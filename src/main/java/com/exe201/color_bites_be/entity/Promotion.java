package com.exe201.color_bites_be.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.DBRef;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Document(collection = "promotions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Promotion {
    @Id
    private String id;

    @DBRef
    private Restaurant restaurant;

    @Field("title")
    private String title;

    @Field("description")
    private String description;

    @Field("start_date")
    private LocalDateTime startDate;

    @Field("end_date")
    private LocalDateTime endDate;

    @Field("price")
    private Double price;

    @Field("type")
    private String type;

    @Field("link")
    private String link;

    @Field("created_at")
    private LocalDateTime createdAt;
}
