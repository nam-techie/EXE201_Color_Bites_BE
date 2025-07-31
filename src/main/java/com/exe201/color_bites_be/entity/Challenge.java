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
import java.util.Map;

@Document(collection = "challenges")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Challenge {
    @Id
    private String id;

    @Field("account_id")
    private String accountId;

    @Field("title")
    private String title;

    @Field("description")
    private String description;

    @Field("type")
    private String type;

    @Field("duration_days")
    private Integer durationDays;

    @Field("status")
    private String status;

    @Field("entries")
    private Map<String, Object> entries;

    @Field("reward_url")
    private String rewardUrl;

    @Field("created_at")
    private LocalDateTime createdAt;
}
