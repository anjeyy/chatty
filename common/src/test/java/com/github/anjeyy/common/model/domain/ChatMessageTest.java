package com.github.anjeyy.common.model.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class ChatMessageTest {

    @Test
    void givenTwoEqualObjects_checkEquality_isTrue() {
        // given
        LocalDateTime localDateTime = LocalDateTime.of(2021, 11, 14, 0, 0);
        ChatMessage first = new ChatMessage("simulated message", "junit", localDateTime);
        ChatMessage second = new ChatMessage("simulated message", "junit", localDateTime);

        // when
        boolean actual = first.equals(second);

        // then
        Assertions.assertThat(actual).isTrue();
    }

    @Test
    void givenTwoUnequalObjects_checkEquality_isFalse() {
        // given
        LocalDateTime localDateTime = LocalDateTime.of(2021, 11, 14, 0, 0);
        ChatMessage first = new ChatMessage("simulated message", "junit", localDateTime);
        ChatMessage second = new ChatMessage("simulated message", "another junit", localDateTime);

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
        ChatMessage first = new ChatMessage("simulated message", "junit", localDateTime);

        // when
        boolean actual = first.equals(object);

        // then
        Assertions.assertThat(actual).isFalse();
    }

    @Test
    void givenFullMessage_checkIsValid_isTrue() {
        // given
        LocalDateTime localDateTime = LocalDateTime.of(2021, 11, 14, 0, 0);
        ChatMessage chatMessage = new ChatMessage("simulated message", "junit", localDateTime);

        // when
        boolean actual = chatMessage.isValid();

        // then
        Assertions.assertThat(actual).isTrue();
    }

    @CsvSource(
            nullValues = {"null"},
            value = {
                    "simulated Message,junit,null",
                    "simulated Message,null,2021-11-14T18:00:00",
                    "null,junit,2021-11-14T18:00:00"
            }
    )
    @ParameterizedTest
    void givenMissingDateMessage_checkIsValid_isFalse(String message, String author, String date) {
        // given
        LocalDateTime localDateTime = null;
        if (date != null) {
            localDateTime = LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME);
        }
        ChatMessage chatMessage = new ChatMessage(message, author, localDateTime);

        // when
        boolean actual = chatMessage.isValid();

        // then
        Assertions.assertThat(actual).isFalse();
    }
}