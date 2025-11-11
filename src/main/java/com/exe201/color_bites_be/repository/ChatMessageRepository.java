package com.exe201.color_bites_be.repository;

import com.exe201.color_bites_be.entity.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, UUID> {
}

