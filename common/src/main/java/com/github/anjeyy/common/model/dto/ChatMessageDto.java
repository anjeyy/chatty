package com.github.anjeyy.common.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.Objects;

public class ChatMessageDto {

    private final String message;
    private final String author;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime created;

    public ChatMessageDto() {
        //necessary for Jackson
        this.message = null;
        this.author = null;
        this.created = null;
    }

    public ChatMessageDto(String message, String author, LocalDateTime created) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChatMessageDto)) return false;
        ChatMessageDto that = (ChatMessageDto) o;
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
        return String.format("[%s] %s~ %s", created, author, message);
    }
}
