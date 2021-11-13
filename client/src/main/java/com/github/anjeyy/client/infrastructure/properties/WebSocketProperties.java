package com.github.anjeyy.client.infrastructure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties(prefix = "websocket")
public class WebSocketProperties {

    private final String url;
    private final String destination;
    private final String subscription;

    public WebSocketProperties(String url, String destination, String subscription) {
        this.url = url;
        this.destination = destination;
        this.subscription = subscription;
    }

    public String getUrl() {
        return url;
    }

    public String getDestination() {
        return destination;
    }

    public String getSubscription() {
        return subscription;
    }
}
