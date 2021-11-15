package com.github.anjeyy.common.model.converter;

import com.github.anjeyy.common.model.domain.ChatMessage;
import com.github.anjeyy.common.model.dto.ChatMessageDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

class ChatMessageConverterTest {

    @Test
    void givenValidDto_convertToDomain_successfully() {
        // given
        LocalDateTime localDateTime = LocalDateTime.of(2021, 11, 14, 0, 0);
        ChatMessageDto chatMessageDto = new ChatMessageDto("simulated message", "junit", localDateTime);
        ChatMessage expected = new ChatMessage("simulated message", "junit", localDateTime);

        // when
        ChatMessage actual = ChatMessageConverter.toDomain(chatMessageDto);

        // then
        Assertions.assertThat(actual)
                .isNotNull()
                .isEqualTo(expected);
    }

    @Test
    void givenNullDto_convertToDomain_successfully() {
        // given
        ChatMessageDto chatMessageDto = null;
        ChatMessage expected = new ChatMessage();

        // when
        ChatMessage actual = ChatMessageConverter.toDomain(chatMessageDto);

        // then
        Assertions.assertThat(actual)
                .isNotNull()
                .hasAllNullFieldsOrProperties()
                .isEqualTo(expected);
    }

    @Test
    void givenValidDomain_convertToDto_successfully() {
        // given
        LocalDateTime localDateTime = LocalDateTime.of(2021, 11, 14, 0, 0);
        ChatMessage chatMessage = new ChatMessage("simulated message", "junit", localDateTime);
        ChatMessageDto expected = new ChatMessageDto("simulated message", "junit", localDateTime);

        // when
        ChatMessageDto actual = ChatMessageConverter.toDto(chatMessage);

        // then
        Assertions.assertThat(actual)
                .isNotNull()
                .isEqualTo(expected);
    }

    @Test
    void givenNullDomain_convertToDto_successfully() {
        // given
        ChatMessage chatMessage = null;
        ChatMessageDto expected = new ChatMessageDto();

        // when
        ChatMessageDto actual = ChatMessageConverter.toDto(chatMessage);

        // then
        Assertions.assertThat(actual)
                .isNotNull()
                .hasAllNullFieldsOrProperties()
                .isEqualTo(expected);
    }

}