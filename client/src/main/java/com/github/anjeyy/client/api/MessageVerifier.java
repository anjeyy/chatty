package com.github.anjeyy.client.api;

import com.github.anjeyy.common.model.dto.ChatMessageDto;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public enum MessageVerifier {

    INSTANCE;

    private final List<ChatMessageDto> unsentMessages = new ArrayList<>();

    public void addMessage(ChatMessageDto chatMessageDto) {
        Optional.ofNullable(chatMessageDto)
                .ifPresent(unsentMessages::add);
    }

    public boolean removeIfPresent(ChatMessageDto chatMessageDto) {
        return Optional.ofNullable(chatMessageDto)
                .map(unsentMessages::remove)
                .orElse(false);
    }
}
