package com.exe201.color_bites_be.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.index.Indexed;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Document(collection = "restaurant_images")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantImages {
    @Id
    private String id;

    @Field("restaurant_id")
    @Indexed
    private String restaurantId;

    @Field("url")
    private String url;

    @Field("sort_order")
    private Integer sortOrder;

    @Field("created_at")
    private LocalDateTime createdAt;
}
