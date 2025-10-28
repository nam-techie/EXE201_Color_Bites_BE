package com.exe201.color_bites_be.controller;

import com.exe201.color_bites_be.dto.request.ChatMessageRequestDto;
import com.exe201.color_bites_be.dto.response.ResponseDto;
import com.exe201.color_bites_be.service.IChatMessageService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Validated
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('USER')")
public class ChatController {

    @Autowired
    private IChatMessageService chatMessageService;

    @PostMapping("/chat-messages")
    public ResponseDto<Object> chatMessages(@Valid @RequestBody ChatMessageRequestDto chatMessageRequestDto) {

        return ResponseDto.builder()
                .status(HttpStatus.OK.value())
                .message(HttpStatus.OK.name())
                .data(chatMessageService.chatMessageWithAdmission(chatMessageRequestDto))
                .build();
    }
}