package com.exe201.color_bites_be.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Document(collection = "restaurant_tags")
@CompoundIndexes({
    @CompoundIndex(name = "restaurant_tag_unique", def = "{'restaurant_id': 1, 'tag_id': 1}", unique = true),
    @CompoundIndex(name = "tag_index", def = "{'tag_id': 1}")
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantTags {
    @Id
    private String id;

    @Field("restaurant_id")
    private String restaurantId;

    @Field("tag_id")
    private String tagId;
}
