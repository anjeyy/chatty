package com.github.anjeyy.common.model.converter;

import com.github.anjeyy.common.model.domain.ChatMessage;
import com.github.anjeyy.common.model.dto.ChatMessageDto;

public class ChatMessageConverter {

    private ChatMessageConverter() {
        throw new UnsupportedOperationException("No instance allowed.");
    }

    public static ChatMessage toDomain(ChatMessageDto chatMessageDto) {
        if (chatMessageDto == null) {
            return new ChatMessage();
        }
        return new ChatMessage(
                chatMessageDto.getMessage(),
                chatMessageDto.getAuthor(),
                chatMessageDto.getCreated()
        );
    }

    public static ChatMessageDto toDto(ChatMessage chatMessage) {
        if (chatMessage == null) {
            return new ChatMessageDto();
        }
        return new ChatMessageDto(
                chatMessage.getMessage(),
                chatMessage.getAuthor(),
                chatMessage.getCreated()
        );
    }
}
