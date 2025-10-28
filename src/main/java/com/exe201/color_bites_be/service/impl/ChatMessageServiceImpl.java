package com.exe201.color_bites_be.service.impl;

import com.exe201.color_bites_be.dto.request.ChatMessageRequestDto;
import com.exe201.color_bites_be.dto.response.SendChatResponseDto;
import com.exe201.color_bites_be.entity.ChatMessage;
import com.exe201.color_bites_be.enums.Sender;
import com.exe201.color_bites_be.mapper.ChatMessageMapper;
import com.exe201.color_bites_be.repository.ChatMessageRepository;
import com.exe201.color_bites_be.service.IChatMessageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements IChatMessageService {

    @Value("${gemini.api.url}")
    String geminiApiUrl;

    @Value("${gemini.api.key}")
    String geminiApiKey;

    final ChatMessageMapper chatMessageMapper;

    final ChatMessageRepository chatMessageRepository;

    @Transactional
    public SendChatResponseDto chatMessageWithAdmission(ChatMessageRequestDto chatMessageRequestDto) {

        ChatMessage chatMessage =
                chatMessageRepository.save(
                        ChatMessage.builder()
                                .content(chatMessageRequestDto.getMessage())
                                .deletedAt(null)
                                .senderId(chatMessageRequestDto.getSenderId())
                                .senderType(Sender.USER.toString().toLowerCase())
                                .build()
                );

        return SendChatResponseDto.builder()
                .chatUserDto(chatMessageMapper.toDto(chatMessage))
                .chatAI(chatMessageMapper.toDto(saveChatAi(chatMessageRequestDto, chatMessage)))
                .build();
    }

    @Transactional
    public ChatMessage saveChatAi(ChatMessageRequestDto chatMessageRequestDto, ChatMessage chatMessage){
        String messageResponseAi = getMessageFromAdmission(chatMessageRequestDto);

        return chatMessageRepository.save(
                ChatMessage.builder()
                        .content(messageResponseAi)
                        .senderType(Sender.BOT.toString().toLowerCase())
                        .build()
        );
    }
    private String getMessageFromAdmission(ChatMessageRequestDto chatMessageRequestDto) {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> requestBody = Map.of(
                "contents", new Object[]{
                        Map.of("parts", new Object[]{
                                Map.of("text", chatMessageRequestDto.getMessage())
                        })
                }
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                geminiApiUrl + geminiApiKey,
                requestEntity,
                String.class
        );

        return response.getBody();
    }
}
