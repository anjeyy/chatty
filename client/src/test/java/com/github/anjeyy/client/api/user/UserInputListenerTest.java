package com.github.anjeyy.client.api.user;

import com.github.anjeyy.common.model.dto.ChatMessageDto;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Optional;

class UserInputListenerTest {

    private final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    private final PrintStream outPrintStream = System.out;

    private UserInputListener uut;

    @BeforeEach
    void setup() {
        System.setOut(new PrintStream(byteArrayOutputStream));
    }

    @AfterEach
    void reset() {
        System.setOut(outPrintStream);
    }

    @Test
    void givenValidInput_determineUsername_firstTrySuccessfully() {
        // given
        String data = "junit-test";
        System.setIn(new ByteArrayInputStream(data.getBytes()));
        uut = new UserInputListener();

        // when
        uut.determineUsername();

        // then
        Assertions.assertThat(byteArrayOutputStream.toString())
                .isNotNull()
                .isNotBlank()
                .contains("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
                .containsOnlyOnce("Before you enter the chat, please enter your name.")
                .containsOnlyOnce("Have fun junit-test, but don't go too wild.");
    }

    @ValueSource(strings = {"\nmyName", "\n myName", " \n myName", " \n myName "})
    @ParameterizedTest
    void givenInvalidInput_determineUsername_afterSecondTrySuccessfully(String userInput) {
        // given
        System.setIn(new ByteArrayInputStream(userInput.getBytes()));
        uut = new UserInputListener();

        // when
        uut.determineUsername();

        // then
        Assertions.assertThat(byteArrayOutputStream.toString())
                .isNotNull()
                .isNotBlank()
                .contains("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
                .containsOnlyOnce("Before you enter the chat, please enter your name.")
                .containsOnlyOnce("Please re-enter your name.")
                .containsOnlyOnce("Have fun myName, but don't go too wild.");
    }

    @Test
    void givenInvalidInput_determineUsername_afterFifthTrySuccessfully() {
        // given
        String userInput = "\n\n\n\n myName";
        System.setIn(new ByteArrayInputStream(userInput.getBytes()));
        uut = new UserInputListener();

        // when
        uut.determineUsername();

        // then
        Assertions.assertThat(byteArrayOutputStream.toString())
                .isNotNull()
                .isNotBlank()
                .contains("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
                .containsOnlyOnce("Before you enter the chat, please enter your name.")
                .contains("Please re-enter your name.")
                .containsOnlyOnce("Have fun myName, but don't go too wild.");
    }

    //todo input message

    @Test
    void givenProperUserInput_preparingToDispatch_isSuccessful() {
        // given
        String userInput = "junit-testname \n my simulated chat message";
        System.setIn(new ByteArrayInputStream(userInput.getBytes()));
        uut = new UserInputListener();
        ChatMessageDto expected = new ChatMessageDto(
                "my simulated chat message",
                "junit-testname",
                null
        );

        // when
        Optional<ChatMessageDto> actual = uut.getInputMessage();

        // then
        Condition<ChatMessageDto> chatMessageIsValid = new Condition<>(
                c -> c.getMessage() != null && c.getAuthor() != null && c.getCreated() != null,
                "Chat message is valid."
        );
        Assertions.assertThat(actual)
                .isNotNull()
                .isPresent()
                .get()
                .is(chatMessageIsValid)
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(expected);
    }

    @Test
    void givenEmptyMessageInput_isConverted_toEmptyOptional() {
        // given
        String userInput = "junit-testname \n  ";
        System.setIn(new ByteArrayInputStream(userInput.getBytes()));
        uut = new UserInputListener();

        // when
        Optional<ChatMessageDto> actual = uut.getInputMessage();

        // then
        Assertions.assertThat(actual)
                .isNotNull()
                .isNotPresent()
                .isEmpty();
    }


}