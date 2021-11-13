package com.github.anjeyy.client.api;

import com.github.anjeyy.common.model.dto.ChatMessageDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ReceivedMessageProcessor {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public void process(Object payload) {
        log.debug("Message received: {}", payload);

        boolean payloadHasWrongFormat = !(payload instanceof ChatMessageDto);
        if (payloadHasWrongFormat) {
            log.warn("Payload with wrong format received: {}", payload);
            return;
        }
        ChatMessageDto chatMessageDto = (ChatMessageDto) payload;

        displayMessage(chatMessageDto);
    }

    private void displayMessage(ChatMessageDto chatMessageDto) {
        boolean isMessageFromOwnClient = MessageVerifier.INSTANCE.removeIfPresent(chatMessageDto);
        if (isMessageFromOwnClient) {
            return;
        }
        System.out.println(chatMessageDto);
    }
}
