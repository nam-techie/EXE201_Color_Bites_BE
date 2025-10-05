package com.exe201.color_bites_be.entity;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.time.LocalDateTime;

@Document(collection = "restaurants")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class Restaurant {

    @Id
    private String id;                 // _id

    @Field("name")
    private String name;

    @Field("address")
    private String address;

    @Field("district")
    private String district;             // VD: "Bình Chánh"

    @Field("type")
    private String type;                 // VD: "Gà"

    @Field("price")
    private String price;                // VD: "15.000 - 45.000" (range -> String)

    @Field("latitude")
    private Double latitude;

    @Field("longitude")
    private Double longitude;

    @Field("created_by")
    private String createdBy;             // lưu UUID user (ref tới accounts)

    @Field("is_deleted")
    private Boolean isDeleted = false;

    @Field("created_at")
    @CreatedDate
    private LocalDateTime createdAt;           // map ISODate trong Mongo
}
