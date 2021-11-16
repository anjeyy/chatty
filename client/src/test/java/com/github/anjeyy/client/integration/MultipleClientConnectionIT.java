package com.github.anjeyy.client.integration;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import java.io.IOException;

@Testcontainers
class MultipleClientConnectionIT {

    @Container
    private static final GenericContainer<?> CHATTY_SERVER =
            new GenericContainer<>(DockerImageName.parse("anjeyy/chatty:server-latest"))
                    .waitingFor(Wait.forHttp("/actuator/health").forStatusCode(200))
                    .withAccessToHost(true)
                    .withExposedPorts(8080);

    @Container
    private static final GenericContainer<?> CHATTY_CLIENT_ONE =
            new GenericContainer<>(DockerImageName.parse("anjeyy/chatty:client-latest"))
                    .waitingFor(Wait.forLogMessage(".*Before you enter the chat, please enter your name.*", 1))
                    .withEnv("websocket.url", "host.docker.internal")
                    .withCreateContainerCmdModifier(it -> it.withTty(true).withStdinOpen(true)/*.withAttachStdin(true)*/);

//    @Container
//    private static final GenericContainer<?> CHATTY_CLIENT_TWO =
//            new GenericContainer<>(DockerImageName.parse("anjeyy/chatty:client-latest"))
//                    .withEnv("websocket.url", "host.docker.internal")
//                    .withCreateContainerCmdModifier(it -> it.withCmd("-ti"));


    @Test
    void abc() throws IOException, InterruptedException {
        // given
        CHATTY_SERVER.start();
        CHATTY_CLIENT_ONE.start();

        //todo write shell script with two clients and test message reception!

        // when
        org.testcontainers.containers.Container.ExecResult lsResult = CHATTY_CLIENT_ONE.execInContainer("ls", "-la");
        String consoleResult = lsResult.getStdout();

        org.testcontainers.containers.Container.ExecResult nameInputResult = CHATTY_CLIENT_ONE.execInContainer("echo", "myTestName", "|", "socat", "EXEC:\"docker attach chatty-client\",pty", "STDIN");
        consoleResult = nameInputResult.getStdout();

        org.testcontainers.containers.Container.ExecResult appStart = CHATTY_CLIENT_ONE.execInContainer("java", "-jar", "client.jar");
        consoleResult = appStart.getStdout();

        org.testcontainers.containers.Container.ExecResult nameResult = CHATTY_CLIENT_ONE.execInContainer("andi");
        consoleResult = nameResult.getStdout();
        //todocheck here
        Thread.sleep(1000L);
        CHATTY_CLIENT_ONE.execInContainer("send a automated test message");

        // then

        System.out.println("###########");
    }
}
