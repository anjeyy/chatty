package com.github.anjeyy.client.infrastructure.config;

import com.github.anjeyy.common.converter.CustomJackson2MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

@Configuration
public class WebSocketConfig {


    @Bean
    public WebSocketStompClient webSocketStompClient() {
        WebSocketClient webSocketClient = new StandardWebSocketClient();
        WebSocketStompClient webSocketStompClient = new WebSocketStompClient(webSocketClient);
        MappingJackson2MessageConverter mappingJackson2MessageConverter = mappingJackson2MessageConverter();
        webSocketStompClient.setMessageConverter(mappingJackson2MessageConverter);
        return webSocketStompClient;
    }

    @Bean
    public MappingJackson2MessageConverter mappingJackson2MessageConverter() {
        return CustomJackson2MessageConverter.mappingJackson2MessageConverter();
    }
}
