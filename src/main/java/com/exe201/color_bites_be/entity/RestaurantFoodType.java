package com.exe201.color_bites_be.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "restaurant_food_types")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@CompoundIndexes({
    @CompoundIndex(name = "restaurant_food_type_unique", def = "{'restaurant_id': 1, 'food_type_id': 1}", unique = true),
    @CompoundIndex(name = "restaurant_id_idx", def = "{'restaurant_id': 1}"),
    @CompoundIndex(name = "food_type_id_idx", def = "{'food_type_id': 1}")
})
public class RestaurantFoodType {

    @Id
    @Field("_id")
    private String id;

    @Field("restaurant_id")
    private String restaurantId;

    @Field("food_type_id")
    private String foodTypeId;

    @Field("created_at")
    @CreatedDate
    private LocalDateTime createdAt;
}
