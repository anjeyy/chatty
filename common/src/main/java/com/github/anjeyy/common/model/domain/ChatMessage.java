package com.github.anjeyy.common.model.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public class ChatMessage {

    private final String message;
    private final String author;
    private final LocalDateTime created;

    public ChatMessage() {
        this.message = null;
        this.author = null;
        this.created = null;
    }

    public ChatMessage(String message, String author, LocalDateTime created) {
        this.message = message;
        this.author = author;
        this.created = created;
    }

    public String getMessage() {
        return message;
    }

    public String getAuthor() {
        return author;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public boolean isNotValid() {
        return !isValid();
    }

    public boolean isValid() {
        return message != null
                && author != null
                && created != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChatMessage)) return false;
        ChatMessage that = (ChatMessage) o;
        return Objects.equals(message, that.message)
                && Objects.equals(author, that.author)
                && Objects.equals(created, that.created);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, author, created);
    }

    @Override
    public String toString() {
        return String.format("ChatMessage~[message=%s, author=%s, created=%s]", message, author, created);
    }

}
