package com.exe201.color_bites_be.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.DBRef;
import com.exe201.color_bites_be.enums.Visibility;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "mood_maps")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MoodMap {
    @Id
    private String id;

    @Field("account_id")
    private String accountId;

    @Field("title")
    private String title;

    @Field("entries")
    private Map<String, Object> entries;

    @Field("visibility")
    private Visibility visibility;

    @Field("exported")
    private Boolean exported;

    @Field("export_url")
    private String exportUrl;

    @Field("created_at")
    private LocalDateTime createdAt;

    @Field("is_deleted")
    private Boolean isDeleted = false;
}
