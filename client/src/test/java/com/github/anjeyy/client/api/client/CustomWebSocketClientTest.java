package com.github.anjeyy.client.api.client;

import com.github.anjeyy.client.api.message.MessageDuplicationVerifier;
import com.github.anjeyy.client.infrastructure.exception.ChatConnectionException;
import com.github.anjeyy.client.infrastructure.properties.WebSocketProperties;
import com.github.anjeyy.common.model.dto.ChatMessageDto;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class CustomWebSocketClientTest {

    @Mock
    private WebSocketStompClient webSocketStompClient;

    @Mock
    private StompSessionHandler stompSessionHandler;

    @Mock
    private WebSocketProperties webSocketProperties;

    @InjectMocks
    private CustomWebSocketClient uut;

    @Mock
    private StompSession session;

    @Mock
    private ListenableFuture<StompSession> stompSessionListenableFuture;

    @Test
    void givenNormalSetting_connectToWs_isSuccessful() throws ExecutionException, InterruptedException {
        // given
        mockConnection();

        // when
        uut.connect();
        uut.connect();
        uut.connect();

        // then
        then(webSocketStompClient).should(times(1)).connect("fakeWsUrl", stompSessionHandler);
    }

    @Test
    void givenRetryConnection_connectToWs_isSuccessful() throws ExecutionException, InterruptedException {
        // given
        given(webSocketProperties.getRetryAttempts()).willReturn(5);
        given(webSocketProperties.getUrl()).willReturn("fakeWsUrl");
        given(webSocketProperties.getRetryTimeout()).willReturn(250L);
        given(webSocketStompClient.connect("fakeWsUrl", stompSessionHandler))
                .willReturn(stompSessionListenableFuture);
        RuntimeException runtimeException = new RuntimeException("Simulate error during establishing a proper connection");
        given(stompSessionListenableFuture.get())
                .willThrow(new ExecutionException(runtimeException))
                .willThrow(new ExecutionException(runtimeException))
                .willThrow(new ExecutionException(runtimeException))
                .willThrow(new ExecutionException(runtimeException))
                .willReturn(session);
        given(session.isConnected()).willReturn(true);

        // when
        uut.connect();
        uut.connect();
        uut.connect();

        // then
        then(webSocketStompClient).should(times(5)).connect("fakeWsUrl", stompSessionHandler);
        then(webSocketProperties).should(times(4 * 2)).getRetryTimeout();
    }

    @Test
    void givenRetryConnection_withTooManyRetries_throwsException() throws ExecutionException, InterruptedException {
        // given
        given(webSocketProperties.getRetryAttempts()).willReturn(5);
        given(webSocketProperties.getUrl()).willReturn("fakeWsUrl");
        given(webSocketProperties.getRetryTimeout()).willReturn(250L);
        given(webSocketStompClient.connect("fakeWsUrl", stompSessionHandler))
                .willReturn(stompSessionListenableFuture);
        RuntimeException runtimeException = new RuntimeException("Simulate error during establishing a proper connection");
        given(stompSessionListenableFuture.get())
                .willThrow(new ExecutionException(runtimeException))
                .willThrow(new ExecutionException(runtimeException))
                .willThrow(new ExecutionException(runtimeException))
                .willThrow(new ExecutionException(runtimeException))
                .willThrow(new ExecutionException(runtimeException));

        // when
        ThrowableAssert.ThrowingCallable expectedThrow = () -> uut.connect();

        // then
        Assertions.assertThatThrownBy(expectedThrow)
                .isInstanceOf(ChatConnectionException.class)
                .hasMessage("No connection could be established after 5 retries.");
        then(webSocketStompClient).should(times(5)).connect("fakeWsUrl", stompSessionHandler);
        then(webSocketProperties).should(times(5 * 2)).getRetryTimeout();
    }

    @Test
    void givenNoConnection_disconnect_doesNothing() {
        // given

        // when
        uut.disconnect();

        // then
        then(session).should(never()).disconnect();
    }

    @Test
    void givenConnection_disconnect_successfully() throws ExecutionException, InterruptedException {
        // given
        mockConnection();
        given(session.isConnected()).willReturn(true).willReturn(false);

        // when
        uut.connect();
        uut.disconnect();
        uut.disconnect();
        uut.disconnect();

        // then
        then(webSocketStompClient).should(times(1)).connect("fakeWsUrl", stompSessionHandler);
        then(session).should(times(1)).disconnect();
    }

    @Test
    void givenPayload_send_successfully() throws ExecutionException, InterruptedException {
        // given
        LocalDateTime localDateTime = LocalDateTime.of(2021, 11, 14, 0, 0);
        ChatMessageDto chatMessageDto = new ChatMessageDto("simulated message", "junit", localDateTime);
        given(webSocketProperties.getDestination()).willReturn("myJunitCustomTopic");
        mockConnection();

        // when
        uut.connect();
        uut.send(chatMessageDto);
        boolean messageWasAdded = MessageDuplicationVerifier.INSTANCE.removeIfPresent(chatMessageDto);

        // then
        Assertions.assertThat(messageWasAdded).isTrue();
    }

    private void mockConnection() throws ExecutionException, InterruptedException {
        given(webSocketProperties.getRetryAttempts()).willReturn(2);
        given(webSocketProperties.getUrl()).willReturn("fakeWsUrl");
        given(webSocketStompClient.connect("fakeWsUrl", stompSessionHandler))
                .willReturn(stompSessionListenableFuture);
        given(stompSessionListenableFuture.get()).willReturn(session);
        given(session.isConnected()).willReturn(true);
    }

}