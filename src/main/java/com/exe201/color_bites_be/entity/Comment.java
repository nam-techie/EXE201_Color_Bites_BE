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

@Document(collection = "comments")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    @Id
    private String id;

    @Field("post_id")
    private String postId;

    @Field("account_id")
    private String accountId;

    @Field("parent_comment_id")
    private String parentCommentId;

    @Field("depth")
    private Integer depth;

    @Field("content")
    private String content;

    @Field("reply_count")
    private Integer replyCount;

    @Field("is_deleted")
    private Boolean isDeleted;

    @Field("created_at")
    private LocalDateTime createdAt;

    @Field("updated_at")
    private LocalDateTime updatedAt;
}
