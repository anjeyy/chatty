package com.github.anjeyy.server.api.service;

import com.github.anjeyy.common.model.converter.ChatMessageConverter;
import com.github.anjeyy.common.model.domain.ChatMessage;
import com.github.anjeyy.common.model.dto.ChatMessageDto;
import com.github.anjeyy.server.infrastructure.exception.InternalServerException;
import org.springframework.stereotype.Service;

@Service
public class ChatMessageService {

    /**
     * Simple method to verify incoming message and in case of invalidity how to handle it.
     *
     * @param chatMessageDto received 'raw' message
     * @throws InternalServerException in case of invalid message
     */
    public void verifyMessage(ChatMessageDto chatMessageDto) {
        ChatMessage chatMessage = ChatMessageConverter.toDomain(chatMessageDto);
        if (chatMessage.isNotValid()) {
            throw new InternalServerException(String.format("Received message %s is invalid.", chatMessage));
        }
    }
}
