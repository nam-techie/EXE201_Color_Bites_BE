package com.exe201.color_bites_be.entity;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.time.LocalDateTime;

@Document(collection = "restaurants")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class Restaurant {

    @Id
    @Field("_id")
    private String id;                 // _id

    // Map tất cả field names từ MongoDB (có thể có cả camelCase và PascalCase)
    @Field("Restaurant Name")
    private String restaurantName;     // Tên trong DB: "Restaurant Name"

    @Field("name")
    private String name;               // Fallback nếu có field "name"

    @Field("Address")
    private String addressCapital;     // Tên trong DB: "Address" (viết hoa)

    @Field("address")
    private String address;            // Fallback nếu có field "address"

    @Field("quận")
    private String quan;               // Tên trong DB: "quận" (tiếng Việt)

    @Field("district")
    private String district;           // Fallback nếu có field "district"

    @Field("Type")
    private String typeCapital;        // Tên trong DB: "Type" (viết hoa)

    @Field("type")
    private String type;               // Fallback: "Gà", "Bún", etc.

    @Field("Price")
    private String priceCapital;       // Tên trong DB: "Price" (viết hoa)

    @Field("price")
    private String price;              // Fallback: "15.000 - 50.000"

    @Field("lat")
    private Double lat;                // Vĩ độ (tên ngắn)

    @Field("latitude")
    private Double latitude;           // Fallback

    @Field("lon")
    private Double lon;                // Kinh độ (tên ngắn)

    @Field("longitude")
    private Double longitude;          // Fallback

    @Field("location")
    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint location;     // GeoJSON Point { type: "Point", coordinates: [lon, lat] }

    @Field("created_by")
    private String createdBy;          // UUID user (ref tới accounts)

    @Field("is_deleted")
    private Boolean isDeleted = false;

    @Field("created_at")
    @CreatedDate
    private LocalDateTime createdAt;   // ISODate trong Mongo

    // Helper methods để lấy giá trị ưu tiên
    public String getDisplayName() {
        return restaurantName != null ? restaurantName : name;
    }

    public String getDisplayAddress() {
        return addressCapital != null ? addressCapital : address;
    }

    public String getDisplayDistrict() {
        return quan != null ? quan : district;
    }

    public String getDisplayType() {
        return typeCapital != null ? typeCapital : type;
    }

    public String getDisplayPrice() {
        return priceCapital != null ? priceCapital : price;
    }

    public Double getDisplayLatitude() {
        return lat != null ? lat : latitude;
    }

    public Double getDisplayLongitude() {
        return lon != null ? lon : longitude;
    }
}
