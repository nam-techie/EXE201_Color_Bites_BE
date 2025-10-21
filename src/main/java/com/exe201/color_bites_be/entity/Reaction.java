package com.exe201.color_bites_be.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import com.exe201.color_bites_be.enums.ReactionType;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Document(collection = "reactions")
@CompoundIndexes({
    @CompoundIndex(name = "post_account_unique", def = "{'post_id': 1, 'account_id': 1}", unique = true)
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Reaction {
    @Id
    private String id;

    @Field("post_id")
    private String postId;

    @Field("account_id")
    private String accountId;

    @Field("reaction")
    private ReactionType reaction;

    @Field("created_at")
    private LocalDateTime createdAt;
} 