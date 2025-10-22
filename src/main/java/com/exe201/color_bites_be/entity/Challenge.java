package com.exe201.color_bites_be.entity;

import com.exe201.color_bites_be.enums.Role;
import com.exe201.color_bites_be.enums.Visibility;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.DBRef;
import com.exe201.color_bites_be.enums.ChallengeStatus;
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

    @Field("create_by")
    private Role createBy;

    @Field("title")
    private String title;

    @Field("description")
    private String description;

    @Field("visibility")
    private Visibility visibility;

    @Field("type")
    private String type;

    @Field("entries")
    private Map<String, Object> entries;

    @Field("reward_url")
    private String rewardUrl;

    @Field("created_at")
    private LocalDateTime createdAt;

    @Field("start_at")
    private  LocalDateTime startAt;

    @Field("expired_at")
    private  LocalDateTime expiredAt;
}
