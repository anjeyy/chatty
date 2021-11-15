package com.github.anjeyy.server.api.service;

import com.github.anjeyy.common.model.dto.ChatMessageDto;
import com.github.anjeyy.server.infrastructure.exception.InternalServerException;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

class ChatMessageServiceTest {

    private final ChatMessageService uut = new ChatMessageService();

    @Test
    void givenNullMessage_verifyMessage_throwsException() {
        // given
        ChatMessageDto chatMessageDto = null;

        // when
        ThrowableAssert.ThrowingCallable expectedThrow = () -> uut.verifyMessage(chatMessageDto);

        // then
        Assertions.assertThatThrownBy(expectedThrow)
                .isInstanceOf(InternalServerException.class)
                .hasMessage("Received message ChatMessage~[message=null, author=null, created=null] is invalid.");
    }

    @Test
    void givenValidMessage_verifyMessage_isVerified() {
        // given
        LocalDateTime localDateTime = LocalDateTime.of(2021, 11, 14, 0, 0);
        ChatMessageDto chatMessageDto = new ChatMessageDto("simulated message", "junit", localDateTime);

        // when-then
        uut.verifyMessage(chatMessageDto);
    }

    @Test
    void givenPartlyInvalidMessage_verifyMessage_isUnverified() {
        // given
        ChatMessageDto chatMessageDto = new ChatMessageDto("simulated message", "junit", null);

        // when
        ThrowableAssert.ThrowingCallable expectedThrow = () -> uut.verifyMessage(chatMessageDto);

        Assertions.assertThatThrownBy(expectedThrow)
                .isInstanceOf(InternalServerException.class)
                .hasMessage("Received message ChatMessage~[message=simulated message, author=junit, created=null] is invalid.");
    }
}