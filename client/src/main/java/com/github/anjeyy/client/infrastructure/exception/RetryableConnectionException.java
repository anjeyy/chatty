package com.github.anjeyy.client.infrastructure.exception;

public class RetryableConnectionException extends RuntimeException {

    public RetryableConnectionException(String message) {
        super(message);
    }

    public RetryableConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
