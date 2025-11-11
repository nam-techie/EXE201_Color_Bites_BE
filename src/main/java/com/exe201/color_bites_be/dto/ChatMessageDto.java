package com.exe201.color_bites_be.dto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class ChatMessageDto {
    String  id;

    String senderType;

    Integer senderId;

    String content;

    LocalDateTime sentAt = LocalDateTime.now();

    LocalDateTime deletedAt;
}
