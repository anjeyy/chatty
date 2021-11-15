package com.github.anjeyy.common.model.dto;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import java.time.LocalDateTime;

class ChatMessageDtoTest {

    @Test
    void givenTwoEqualObjects_checkEquality_isTrue() {
        // given
        LocalDateTime localDateTime = LocalDateTime.of(2021, 11, 14, 0, 0);
        ChatMessageDto first = new ChatMessageDto("simulated message", "junit", localDateTime);
        ChatMessageDto second = new ChatMessageDto("simulated message", "junit", localDateTime);

        // when
        boolean actual = first.equals(second);

        // then
        Assertions.assertThat(actual).isTrue();
    }

    @Test
    void givenTwoUnequalObjects_checkEquality_isFalse() {
        // given
        LocalDateTime localDateTime = LocalDateTime.of(2021, 11, 14, 0, 0);
        ChatMessageDto first = new ChatMessageDto("simulated message", "junit", localDateTime);
        ChatMessageDto second = new ChatMessageDto("simulated message", "another junit", localDateTime);

        // when
        boolean actual = first.equals(second);

        // then
        Assertions.assertThat(actual).isFalse();
    }

    @NullAndEmptySource
    @ValueSource(strings = "a random string")
    @ParameterizedTest
    void givenNullAndMessage_checkEquality_isFalse(String object) {
        // given
        LocalDateTime localDateTime = LocalDateTime.of(2021, 11, 14, 0, 0);
        ChatMessageDto first = new ChatMessageDto("simulated message", "junit", localDateTime);

        // when
        boolean actual = first.equals(object);

        // then
        Assertions.assertThat(actual).isFalse();
    }

}