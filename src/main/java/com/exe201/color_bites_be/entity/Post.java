package com.exe201.color_bites_be.entity;

import com.exe201.color_bites_be.enums.Visibility;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.DBRef;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Document(collection = "posts")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Post {
    @Id
    private String id;

    @Field("account_id")
    private String accountId;

    @Field("content")
    private String content;

    @Field("mood_id")
    private String moodId;

    @Field("video_url")
    private String videoUrl;

    @Field("reaction_count")
    private Integer reactionCount;

    @Field("comment_count")
    private Integer commentCount;

    @Field("is_deleted")
    private Boolean isDeleted;

    @Field("visibility")
    private Visibility visibility;

    @Field("created_at")
    private LocalDateTime createdAt;

    @Field("updated_at")
    private LocalDateTime updatedAt;
}
