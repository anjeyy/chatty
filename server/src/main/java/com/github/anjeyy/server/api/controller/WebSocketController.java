package com.github.anjeyy.server.api.controller;

import com.github.anjeyy.common.model.dto.ChatMessageDto;
import com.github.anjeyy.server.api.service.ChatMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final ChatMessageService chatMessageService;

    public WebSocketController(ChatMessageService chatMessageService) {
        this.chatMessageService = chatMessageService;
    }

    @MessageMapping("/chat")
    @SendTo("/topic/chat")
    public ChatMessageDto send(Message<ChatMessageDto> message) {
        if (message == null) {
            log.warn("'null' message received.");
            throw new IllegalStateException("'null' message received.");
        }
        ChatMessageDto chatMessageDto = message.getPayload();
        chatMessageService.verifyMessage(chatMessageDto);
        return chatMessageDto;
    }
}
