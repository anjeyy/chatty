package com.github.anjeyy.client.api;

import com.github.anjeyy.client.api.client.CustomWebSocketClient;
import com.github.anjeyy.client.api.user.UserInputListener;
import com.github.anjeyy.client.infrastructure.exception.ChatConnectionException;
import com.github.anjeyy.common.model.dto.ChatMessageDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class MainEntryPointTest {

    @Mock
    private CustomWebSocketClient customWebSocketClient;

    @Mock
    private UserInputListener userInputListener;

    @InjectMocks
    private MainEntryPoint uut;

    @Test
    void givenEmptyMessageInput_readsAsOptional_preventsMessageFromBeingSent() throws Exception {
        // given
        given(userInputListener.getInputMessage())
                .willReturn(Optional.empty())
                .willReturn(Optional.empty())
                .willThrow(new ChatConnectionException("Throw needed to exit test case."));

        // when
        uut.run();

        // then
        then(customWebSocketClient).should(times(1)).connect();
        then(userInputListener).should(times(3)).getInputMessage();
        then(customWebSocketClient).should(never()).send(any());
        then(customWebSocketClient).should(times(1)).disconnect();
    }

    @Test
    void givenMessageInput_readsAsNormalMessage_isBeingSent() throws Exception {
        // given
        LocalDateTime localDateTime = LocalDateTime.of(2021, 11, 14, 0, 0);
        ChatMessageDto chatMessageDto = new ChatMessageDto("simulated message", "junit", localDateTime);
        given(userInputListener.getInputMessage())
                .willReturn(Optional.of(chatMessageDto))
                .willReturn(Optional.of(chatMessageDto))
                .willThrow(new ChatConnectionException("Throw needed to exit test case."));

        // when
        uut.run();

        // then
        then(customWebSocketClient).should(times(1)).connect();
        then(userInputListener).should(times(3)).getInputMessage();
        then(customWebSocketClient).should(times(2)).send(chatMessageDto);
        then(customWebSocketClient).should(times(1)).disconnect();
    }

}