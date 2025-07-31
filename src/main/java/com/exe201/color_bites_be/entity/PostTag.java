package com.exe201.color_bites_be.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Document(collection = "post_tags")
@CompoundIndexes({
    @CompoundIndex(name = "post_tag_unique", def = "{'post_id': 1, 'tag_id': 1}", unique = true),
    @CompoundIndex(name = "tag_index", def = "{'tag_id': 1}")
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostTag {
    @Id
    private String id;

    @Field("post_id")
    private String postId;

    @Field("tag_id")
    private String tagId;
} 