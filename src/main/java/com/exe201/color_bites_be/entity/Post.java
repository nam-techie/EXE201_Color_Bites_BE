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

    @DBRef
    private Account account;

    @Field("title")
    private String title;

    @Field("content")
    private String content;

    @Field("mood")
    private String mood;

    @Field("image_urls")
    private List<String> imageUrls;

    @Field("video_url")
    private String videoUrl;

    @Field("tags")
    private List<String> tags;

    @Field("reactions")
    private Map<String, Object> reactions;

    @Field("created_at")
    private LocalDateTime createdAt;

    @Field("updated_at")
    private LocalDateTime updatedAt;
}
