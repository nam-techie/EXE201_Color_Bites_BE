package com.exe201.color_bites_be.entity;

import com.exe201.color_bites_be.dto.request.TypeObjectRequest;
import com.exe201.color_bites_be.dto.request.ImageObjectRequest;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "restaurants")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class Restaurant {

    @Id
    private String id;

    @Field("name")
    private String name;

    @Field("address")
    private String address;

    @Field("longitude")
    private BigDecimal longitude;

    @Field("latitude")
    private BigDecimal latitude;

    @Field("location")
    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint location;

    @Field("district")
    private String district;

    @Field("types")
    private List<TypeObjectRequest> types; // JSON array embedded

    @Field("images")
    private List<ImageObjectRequest> images; // JSON array embedded

    @Field("price")
    private String price; // Changed from avgPrice (Double) to price (String)

    @Field("rating")
    private Double rating;

    @Field("featured")
    @Indexed
    private Boolean featured = false;

    @Field("created_by")
    @Indexed
    private String createdBy;

    @Field("created_at")
    @CreatedDate
    private LocalDateTime createdAt;

    @Field("updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Field("is_deleted")
    @Indexed
    private Boolean isDeleted = false;
}
