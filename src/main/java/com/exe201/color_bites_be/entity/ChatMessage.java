package com.exe201.color_bites_be.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "chat_message")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    @Id
    String id;

    @Field("sender_type")
    String senderType;

    @Field("sender_id")
    Integer senderId;

    @Field("content")
    String content;

    @Builder.Default
    @Field("sent_at")
    LocalDateTime sentAt = LocalDateTime.now();

    @Field("deleted_at")
    LocalDateTime deletedAt;
}
