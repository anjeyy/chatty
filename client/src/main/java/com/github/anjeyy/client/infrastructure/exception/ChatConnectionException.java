package com.github.anjeyy.client.infrastructure.exception;

public class ChatConnectionException extends RuntimeException {

    public ChatConnectionException(String message) {
        super(message);
    }

    public ChatConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
