package com.github.anjeyy.server.api.controller;

import com.github.anjeyy.common.model.dto.ChatMessageDto;
import com.github.anjeyy.server.api.service.ChatMessageService;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import java.time.LocalDateTime;
import static org.mockito.BDDMockito.willDoNothing;

@ExtendWith(MockitoExtension.class)
class WebSocketControllerTest {

    @Mock
    private ChatMessageService chatMessageService;

    @InjectMocks
    private WebSocketController uut;

    @Test
    void givenNullMessage_receivePayload_throwsException() {
        // given
        Message<ChatMessageDto> testInput = null;

        // when
        ThrowableAssert.ThrowingCallable expectedThrow = () -> uut.send(testInput);

        // then
        Assertions.assertThatThrownBy(expectedThrow)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("'null' message received.");
    }

    @Test
    void givenValidMessage_receivePayload_redirectsSuccessfully() {
        // given
        LocalDateTime localDateTime = LocalDateTime.of(2021, 11, 14, 0, 0);
        ChatMessageDto expected = new ChatMessageDto("simulated message", "junit", localDateTime);
        Message<ChatMessageDto> testInput = new GenericMessage<>(expected);
        willDoNothing().given(chatMessageService).verifyMessage(expected);


        // when
        ChatMessageDto actual = uut.send(testInput);

        // then
        Assertions.assertThat(actual)
                .isNotNull()
                .isEqualTo(expected);
    }

}