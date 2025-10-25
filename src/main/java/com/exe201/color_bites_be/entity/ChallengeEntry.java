package com.exe201.color_bites_be.entity;

import com.exe201.color_bites_be.enums.EntryStatus;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document(collection = "challenge_entries")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@CompoundIndexes({
    @CompoundIndex(name = "participation_id_idx", def = "{'participation_id': 1}"),
    @CompoundIndex(name = "restaurant_id_idx", def = "{'restaurant_id': 1}"),
    @CompoundIndex(name = "status_idx", def = "{'status': 1}")
})
public class ChallengeEntry {

    @Id
    @Field("_id")
    private String id;

    @Field("participation_id")
    @Indexed
    private String participationId;

    @Field("restaurant_id")
    @Indexed
    private String restaurantId;

    @Field("photo_url")
    private String photoUrl;

    @Field("latitude")
    private BigDecimal latitude;

    @Field("longitude")
    private BigDecimal longitude;

    @Field("status")
    @Indexed
    private EntryStatus status;

    @Field("notes")
    private String notes;

    @Field("created_at")
    @CreatedDate
    private LocalDateTime createdAt;
}





