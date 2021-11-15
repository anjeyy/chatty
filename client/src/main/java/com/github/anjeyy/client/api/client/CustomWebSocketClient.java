package com.github.anjeyy.client.api.client;

import com.github.anjeyy.client.api.message.MessageDuplicationVerifier;
import com.github.anjeyy.client.infrastructure.exception.ChatConnectionException;
import com.github.anjeyy.client.infrastructure.exception.RetryableConnectionException;
import com.github.anjeyy.client.infrastructure.properties.WebSocketProperties;
import com.github.anjeyy.common.model.dto.ChatMessageDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import java.util.concurrent.ExecutionException;

@Component
public class CustomWebSocketClient {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final WebSocketStompClient webSocketStompClient;
    private final StompSessionHandler stompSessionHandler;
    private final WebSocketProperties webSocketProperties;

    private StompSession currentStompSession;

    public CustomWebSocketClient(
            WebSocketStompClient webSocketStompClient,
            StompSessionHandler stompSessionHandler,
            WebSocketProperties webSocketProperties
    ) {
        this.webSocketStompClient = webSocketStompClient;
        this.stompSessionHandler = stompSessionHandler;
        this.webSocketProperties = webSocketProperties;
    }

    public void send(ChatMessageDto payload) {
        if (isConnectionNotEstablished()) {
            connect();
        }

        String topicSink = webSocketProperties.getDestination();
        currentStompSession.send(topicSink, payload);
        MessageDuplicationVerifier.INSTANCE.addMessage(payload);
    }

    public void connect() {
        if (isConnectionEstablished()) {
            return;
        }

        int currentRetryCounter = 0;
        while (currentRetryCounter < webSocketProperties.getRetryAttempts()) {
            String serverUrl = webSocketProperties.getUrl();
            ListenableFuture<StompSession> websocketConnection =
                    webSocketStompClient.connect(serverUrl, stompSessionHandler);
            try {
                waitForEstablishedConnection(websocketConnection);
                break;
            } catch (RetryableConnectionException e) {
                waitForAnotherRetry(currentRetryCounter);
                currentRetryCounter++;
            }
        }

        if (currentRetryCounter == webSocketProperties.getRetryAttempts()) {
            throw new ChatConnectionException(
                    String.format("No connection could be established after %d retries.", currentRetryCounter)
            );
        }
        System.out.println(" ~~ Connection to the Chatroom established. ~~");
        System.out.println(" ~~ Say hi to the others. ~~");
    }

    private void waitForEstablishedConnection(ListenableFuture<StompSession> websocketConnection) {
        websocketConnection.addCallback(
                result -> log.info("Connection successfully established."),
                ex -> log.info("Connection could NOT be established due to an error:", ex.getCause())
        );

        try {
            currentStompSession = websocketConnection.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RetryableConnectionException("An error regarding threads occurred: ", e);
        } catch (ExecutionException e) {
            throw new RetryableConnectionException("Connection of chat-server could not be established: ", e);
        }
    }

    private void waitForAnotherRetry(int currentAttempt) {
        try {
            System.out.printf(
                    "Waiting %d s for another retry.. (%d/%d)%n",
                    webSocketProperties.getRetryTimeout() / 1000,
                    currentAttempt + 1,
                    webSocketProperties.getRetryAttempts()
            );
            Thread.sleep(webSocketProperties.getRetryTimeout());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ChatConnectionException("An error regarding threads occurred: ", e);
        }
    }

    public void disconnect() {
        if (isConnectionNotEstablished()) {
            return;
        }
        currentStompSession.disconnect();
    }

    private boolean isConnectionNotEstablished() {
        return !isConnectionEstablished();
    }

    private boolean isConnectionEstablished() {
        return currentStompSession != null
                && currentStompSession.isConnected();
    }
}
