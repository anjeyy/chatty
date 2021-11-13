package com.github.anjeyy.client.api.client;

import com.github.anjeyy.client.api.MessageVerifier;
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

    public void connect() {
        if (isConnectionEstablished()) {
            return;
        }
        String serverUrl = webSocketProperties.getUrl();
        ListenableFuture<StompSession> websocketConnection = webSocketStompClient.connect(serverUrl, stompSessionHandler);
        waitForEstablishedConnection(websocketConnection);
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
            e.printStackTrace();
            throw new RuntimeException("EEE");  //todo appropriate exceptions
        } catch (ExecutionException e) {
            e.printStackTrace();
            throw new RuntimeException("EEE");
        }
    }

    public void send(ChatMessageDto payload) {
        if (isConnectionNotEstablished()) {
            return;
        }

        String topicSink = webSocketProperties.getDestination();
        currentStompSession.send(topicSink, payload);
        MessageVerifier.INSTANCE.addMessage(payload);
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
