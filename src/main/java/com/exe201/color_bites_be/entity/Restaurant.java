package com.exe201.color_bites_be.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "restaurants")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Restaurant {
    @Id
    private String id;

    @Field("name")
    private String name;

    @Field("address")
    private String address;

    @Field("longitude")
    private Double longitude;

    @Field("latitude")
    private Double latitude;

    @Field("description")
    private String description;

    @Field("type")
    private String type;

    // Removed mood_tags - will be handled by RestaurantTags entity

    @Field("region")
    private String region;

    // Removed image_urls - will be handled by RestaurantImages entity

    @Field("avg_price")
    private Double avgPrice;

    @Field("rating")
    private Double rating;

    @Field("featured")
    private Boolean featured;

    @Field("created_by")
    private String createdBy;

    @Field("created_at")
    private LocalDateTime createdAt;

    @Field("is_deleted")
    private Boolean isDeleted = false;
}
