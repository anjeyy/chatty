package com.github.anjeyy.client.api.client;

import com.github.anjeyy.client.api.message.ReceivedMessageProcessor;
import com.github.anjeyy.client.infrastructure.exception.RetryableConnectionException;
import com.github.anjeyy.client.infrastructure.properties.WebSocketProperties;
import com.github.anjeyy.common.model.dto.ChatMessageDto;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.stomp.ConnectionLostException;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import javax.websocket.DeploymentException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class ChatStompSessionHandlerTest {

    @Mock
    private WebSocketProperties webSocketProperties;

    @Mock
    private ReceivedMessageProcessor receivedMessageProcessor;

    @InjectMocks
    private ChatStompSessionHandler uut;

    @Mock
    private StompSession session;

    @Mock
    private StompSession.Subscription subscription;


    @Test
    void givenCustomStompSessionHandler_afterConnected_isSuccessful() {
        // given
        String topic = "my-junit-topic";
        given(webSocketProperties.getSubscription()).willReturn(topic);
        given(session.subscribe(topic, uut)).willReturn(subscription);
        given(subscription.getSubscriptionId()).willReturn("some fake ID");

        // when
        uut.afterConnected(session, null);

        // then
        then(webSocketProperties).should(times(1)).getSubscription();
        then(session).should(times(1)).subscribe(any(String.class), any());
        then(subscription).should(times(1)).getSubscriptionId();
    }

    @Test
    void givenCustomStompSessionHandler_getPayloadType_isChatMessageClass() {
        // given
        StompHeaders stompHeaders = null;
        Type expected = ChatMessageDto.class;

        // when
        Type actual = uut.getPayloadType(stompHeaders);

        // then
        Assertions.assertThat(actual)
                .isNotNull()
                .isEqualTo(expected);
    }

    @Test
    void givenNullPayload_handleFrame_doesNotDisplayMessage() {
        // given
        StompHeaders stompHeaders = null;
        Object payload = null;

        // when
        uut.handleFrame(stompHeaders, payload);

        // then
        then(receivedMessageProcessor).should(never()).process(any());
    }

    @Test
    void givenValidPayload_handleFrame_displayMessage() {
        // given
        StompHeaders stompHeaders = null;
        LocalDateTime localDateTime = LocalDateTime.of(2021, 11, 14, 0, 0);
        ChatMessageDto payload = new ChatMessageDto("simulated message", "junit", localDateTime);

        // when
        uut.handleFrame(stompHeaders, payload);

        // then
        then(receivedMessageProcessor).should(times(1)).process(payload);
    }

    @Test
    void givenLostConnection_handleTransportError_throwError() {
        // given
        ConnectionLostException exception = new ConnectionLostException("Simulate closed connection by server.");

        // when
        ThrowableAssert.ThrowingCallable expectedThrow = () -> uut.handleTransportError(session, exception);

        // then
        Assertions.assertThatThrownBy(expectedThrow)
                .isInstanceOf(RetryableConnectionException.class)
                .hasMessage("Connection to server suddenly lost: ");
    }

    @Test
    void givenTransportFailure_handleTransportError_throwError() {
        // given
        DeploymentException exception = new DeploymentException("Simulate websocket (client) error.");

        // when
        ThrowableAssert.ThrowingCallable expectedThrow = () -> uut.handleTransportError(session, exception);

        // then
        Assertions.assertThatThrownBy(expectedThrow)
                .isInstanceOf(RetryableConnectionException.class)
                .hasMessage("No connection to server: ");
    }

}