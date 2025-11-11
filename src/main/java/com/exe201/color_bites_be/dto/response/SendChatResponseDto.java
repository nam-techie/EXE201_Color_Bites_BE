package com.exe201.color_bites_be.dto.response;

import com.exe201.color_bites_be.dto.ChatMessageDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SendChatResponseDto {

    ChatMessageDto chatUserDto;

    ChatMessageDto chatAI;
}

