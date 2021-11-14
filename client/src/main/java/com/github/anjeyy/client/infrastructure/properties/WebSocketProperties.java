package com.github.anjeyy.client.infrastructure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties(prefix = "websocket")
public class WebSocketProperties {

    private final String url;
    private final String destination;
    private final String subscription;
    private final int retryAttempts;
    private final long retryTimeout;

    public WebSocketProperties(
            String url,
            String destination,
            String subscription,
            int retryAttempts,
            long retryTimeout
    ) {
        this.url = url;
        this.destination = destination;
        this.subscription = subscription;
        this.retryAttempts = retryAttempts;
        this.retryTimeout = retryTimeout;
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

    public int getRetryAttempts() {
        return retryAttempts;
    }

    public long getRetryTimeout() {
        return retryTimeout;
    }
}
