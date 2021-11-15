package com.github.anjeyy.client.api.client;

import com.github.anjeyy.client.api.message.ReceivedMessageProcessor;
import com.github.anjeyy.client.infrastructure.exception.RetryableConnectionException;
import com.github.anjeyy.client.infrastructure.properties.WebSocketProperties;
import com.github.anjeyy.common.model.dto.ChatMessageDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.stereotype.Component;
import javax.websocket.DeploymentException;
import java.lang.reflect.Type;

@Component
public class ChatStompSessionHandler implements StompSessionHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final WebSocketProperties webSocketProperties;
    private final ReceivedMessageProcessor receivedMessageProcessor;

    public ChatStompSessionHandler(
            WebSocketProperties webSocketProperties,
            ReceivedMessageProcessor receivedMessageProcessor
    ) {
        this.webSocketProperties = webSocketProperties;
        this.receivedMessageProcessor = receivedMessageProcessor;
    }

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        String topic = webSocketProperties.getSubscription();
        StompSession.Subscription subscription = session.subscribe(topic, this);
        log.info("Currently subscribed to {}", topic);
        log.info("Subscription ID: {}", subscription.getSubscriptionId());
    }

    @Override
    public void handleException(
            StompSession session,
            StompCommand command,
            StompHeaders headers,
            byte[] payload,
            Throwable exception
    ) {
        log.error("Error: ", exception);
    }

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
        if (exception instanceof ConnectionLostException) {
            System.out.println(" ~~ Connection to the Chatroom lost. ~~");
            System.out.println(" ~~ Enter a message trying to reconnect and send that message. ~~");
            throw new RetryableConnectionException("Connection to server suddenly lost: ", exception);
        } else if (exception instanceof DeploymentException) {
            throw new RetryableConnectionException("No connection to server: ", exception);
        }
        log.error("Error: ", exception);
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return ChatMessageDto.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        if (payload == null) {
            return;
        }
        receivedMessageProcessor.process(payload);
    }
}
