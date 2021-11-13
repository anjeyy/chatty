package com.github.anjeyy.client.api;

import com.github.anjeyy.common.model.dto.ChatMessageDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

class MessageVerifierTest {

    @Test
    void givenTwoMessages_removingWithThirdSameMessage_isSuccessful() {
        // given
        LocalDateTime localDateTime = LocalDateTime.of(2021, 11, 13, 0, 0, 0);
        ChatMessageDto first = new ChatMessageDto("a msg to test", "author1", localDateTime);
        ChatMessageDto second = new ChatMessageDto("a msg to test", "author2", localDateTime);

        MessageVerifier.INSTANCE.addMessage(first);
        MessageVerifier.INSTANCE.addMessage(second);

        // when
        ChatMessageDto third = new ChatMessageDto("a msg to test", "author1", localDateTime);
        boolean removed = MessageVerifier.INSTANCE.removeIfPresent(third);

        // then
        Assertions.assertThat(removed).isTrue();
    }


    @Test
    void givenForeignMessage_removing_isSuccessful() {
        // given
        LocalDateTime localDateTime = LocalDateTime.of(2021, 11, 13, 0, 0, 0);
        ChatMessageDto chatMessageDto = new ChatMessageDto("a msg to test", "author1", localDateTime);

        // when
        boolean actual = MessageVerifier.INSTANCE.removeIfPresent(chatMessageDto);

        // then
        Assertions.assertThat(actual).isFalse();
    }

}