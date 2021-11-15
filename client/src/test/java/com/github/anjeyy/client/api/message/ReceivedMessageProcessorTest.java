package com.github.anjeyy.client.api.message;

import com.github.anjeyy.common.model.dto.ChatMessageDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;

class ReceivedMessageProcessorTest {

    private final ReceivedMessageProcessor uut = new ReceivedMessageProcessor();
    private final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    private final PrintStream outPrintStream = System.out;

    @BeforeEach
    void setup() {
        System.setOut(new PrintStream(byteArrayOutputStream));
    }

    @Test
    void givenPayload_withWrongFormat_doesNotDisplayMessage() {
        // given
        String testInput = "some fake payload format from wrong client";

        // when
        uut.process(testInput);

        // then
        Assertions.assertThat(byteArrayOutputStream.toString())
                .isNotNull()
                .isNotBlank()
                .contains("Payload with wrong format received: some fake payload format from wrong client");
    }

    @Test
    void givenPayload_withCorrectFormat_displaysMessage() {
        // given
        LocalDateTime localDateTime = LocalDateTime.of(2021, 11, 14, 0, 0);
        ChatMessageDto chatMessageDto = new ChatMessageDto("simulated message", "junit", localDateTime);
        // when
        uut.process(chatMessageDto);

        // then
        Assertions.assertThat(byteArrayOutputStream.toString())
                .isNotNull()
                .isNotBlank()
                .contains("[2021-11-14T00:00] junit~ simulated message");
    }

    @AfterEach
    void reset() {
        System.setOut(outPrintStream);
    }
}