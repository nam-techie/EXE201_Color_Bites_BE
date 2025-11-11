package com.exe201.color_bites_be.service;

import com.exe201.color_bites_be.dto.request.ChatMessageRequestDto;
import com.exe201.color_bites_be.dto.response.SendChatResponseDto;

public interface IChatMessageService {
    SendChatResponseDto chatMessageWithAdmission(ChatMessageRequestDto chatMessageRequestDto);
}
