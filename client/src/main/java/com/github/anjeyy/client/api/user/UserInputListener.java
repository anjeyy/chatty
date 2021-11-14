package com.github.anjeyy.client.api.user;

import com.github.anjeyy.common.model.dto.ChatMessageDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Scanner;

@Component
public class UserInputListener {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final Scanner scanner = new Scanner(System.in);

    private String username;

    public Optional<ChatMessageDto> getConvertedMessage() {
        if (username == null) {
            determineUsername();
        }
        String rawMessage = scanner.nextLine();
        return createFromRawMessage(rawMessage);
    }

    public void determineUsername() {
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
        System.out.println("Before you enter the chat, please enter your name.");
        username = scanner.nextLine();
        System.out.printf("Have fun %s, but don't go too wild.%n%n", username);
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
    }

    private Optional<ChatMessageDto> createFromRawMessage(String rawMessage) {
        if (!StringUtils.hasText(rawMessage)) {
            return Optional.empty();
        }
        LocalDateTime localDateTime = createWithAppropriateFormat();
        ChatMessageDto chatMessageDto = new ChatMessageDto(
                rawMessage,
                username,
                localDateTime
        );
        return Optional.of(chatMessageDto);
    }

    private LocalDateTime createWithAppropriateFormat() {
        LocalDateTime localDateTime = LocalDateTime.now();
        return localDateTime.truncatedTo(ChronoUnit.SECONDS);
    }

}
