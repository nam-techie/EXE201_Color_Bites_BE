package com.exe201.color_bites_be.entity;

import com.exe201.color_bites_be.enums.ChallengeType;
import com.exe201.color_bites_be.model.TypeObject;
import com.exe201.color_bites_be.model.ImageObject;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "challenge_definitions")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@CompoundIndexes({
    @CompoundIndex(name = "challenge_type_idx", def = "{'challenge_type': 1}"),
    @CompoundIndex(name = "restaurant_id_idx", def = "{'restaurant_id': 1}"),
    @CompoundIndex(name = "start_date_idx", def = "{'start_date': 1}"),
    @CompoundIndex(name = "end_date_idx", def = "{'end_date': 1}"),
    @CompoundIndex(name = "is_active_idx", def = "{'is_active': 1}")
})
public class ChallengeDefinition {

    @Id
    @Field("_id")
    private String id;

    @Field("title")
    private String title;

    @Field("description")
    private String description;

    @Field("challenge_type")
    @Indexed
    private ChallengeType challengeType;

    @Field("restaurant_id")
    private String restaurantId; // for PARTNER_LOCATION type

    @Field("type_obj")
    private TypeObject typeObj; // JSON object embedded for THEME_COUNT type

    @Field("images")
    private List<ImageObject> images; // JSON array embedded

    @Field("target_count")
    private Integer targetCount;

    @Field("start_date")
    private LocalDateTime startDate;

    @Field("end_date")
    private LocalDateTime endDate;

    @Field("reward_description")
    private String rewardDescription;

    @Field("created_by")
    private String createdBy;

    @Field("created_at")
    @CreatedDate
    private LocalDateTime createdAt;

    @Field("is_active")
    @Indexed
    private Boolean isActive = true;
}

