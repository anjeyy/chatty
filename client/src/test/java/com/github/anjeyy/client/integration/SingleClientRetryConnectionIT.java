package com.github.anjeyy.client.integration;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

@Testcontainers
@SpringBootTest
@ExtendWith(SpringExtension.class)
class SingleClientRetryConnectionIT {

    private static final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    private static final PrintStream outPrintStream = System.out;

    @Container
    private static final GenericContainer<?> CHATTY_SERVER =
            new GenericContainer<>(DockerImageName.parse("anjeyy/chatty:server-latest"))
                    .waitingFor(Wait.forHttp("/actuator/health").forStatusCode(200))
                    .withAccessToHost(true)
                    .withExposedPorts(8080);

    @BeforeEach
    void setup() {
        System.setOut(new PrintStream(byteArrayOutputStream));
    }

    @AfterEach
    void reset() {
        System.setOut(outPrintStream);
    }

    @BeforeAll
    static void setupBeforeAll() {
        System.setOut(new PrintStream(byteArrayOutputStream));
        setupUserinput();
    }

    @AfterAll
    static void resetAfterAll() {
        System.setOut(outPrintStream);
    }

    private static void setupUserinput() {
        String userInput = "\n junit integration test \n junit automated test message";
        System.setIn(new ByteArrayInputStream(userInput.getBytes()));
    }

    @DynamicPropertySource
    private static void setupEnvironment(DynamicPropertyRegistry registry) {
        int outsidePort = CHATTY_SERVER.getMappedPort(8080);
        String host = CHATTY_SERVER.getHost();
        String testUrl = String.format("ws://%s:%s/chat", host, outsidePort);
//        registry.add("websocket.url", () -> testUrl);
    }

    @Test
    void givenServer_isReadyToConnect_clientConnectsSuccessfully() {
        // given

        // when
        CHATTY_SERVER.start();

        //todo create docker
        //todo set env var ->
        // https://stackoverflow.com/questions/24319662/from-inside-of-a-docker-container-how-do-i-connect-to-the-localhost-of-the-mach
        //todo execute jar inside docker -> verify results

        // then
        Assertions.assertThat(byteArrayOutputStream.toString())
                .isNotNull()
                .isNotBlank()
                .contains("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
                .containsOnlyOnce("Before you enter the chat, please enter your name.")
                .containsOnlyOnce("Please re-enter your name.")
                .containsOnlyOnce("Have fun junit integration test, but don't go too wild.")
                .containsOnlyOnce("~~ Connection to the Chatroom established. ~~")
                .containsOnlyOnce(" ~~ Say hi to the others. ~~");


        System.out.println("##########");
    }
}
