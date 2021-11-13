package com.github.anjeyy.client.infrastructure;

import com.github.anjeyy.client.api.ReceivedMessageProcessor;
import com.github.anjeyy.client.infrastructure.properties.WebSocketProperties;
import com.github.anjeyy.common.model.dto.ChatMessageDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.stereotype.Component;
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
        //todo org.springframework.messaging.simp.stomp.ConnectionLostException: Connection closed -> try periodic reconnect
        //close connection after some tries
        log.error("Error: ", exception);
        exception.printStackTrace();
    }

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
        if (exception instanceof ConnectionLostException) {
            //todo try to reestablish a connection after some time
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

    // https://stackoverflow.com/a/37451459/11770752 - ConnectionLostException:
}
