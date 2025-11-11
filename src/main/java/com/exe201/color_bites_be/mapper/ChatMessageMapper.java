package com.exe201.color_bites_be.mapper;


import com.exe201.color_bites_be.dto.ChatMessageDto;
import com.exe201.color_bites_be.entity.ChatMessage;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;


import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ChatMessageMapper {

    List<ChatMessageDto> toDtoList(List<ChatMessage> chatMessage);

    ChatMessageDto toDto(ChatMessage chatMessage);
}
