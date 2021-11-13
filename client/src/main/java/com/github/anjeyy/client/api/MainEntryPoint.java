package com.github.anjeyy.client.api;

import com.github.anjeyy.client.api.client.CustomWebSocketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class MainEntryPoint implements CommandLineRunner {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final CustomWebSocketClient customWebSocketClient;
    private final UserInputListener userInputListener;

    public MainEntryPoint(CustomWebSocketClient customWebSocketClient, UserInputListener userInputListener) {
        this.customWebSocketClient = customWebSocketClient;
        this.userInputListener = userInputListener;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting perichat - real time chat application..");

        userInputListener.determineUsername();

        customWebSocketClient.connect();    //todo also in while check, then reestablishing if possible!
        try {
            while (true) {
                userInputListener.getConvertedMessage()
                        .ifPresent(m -> customWebSocketClient.send(m));
            }
        } catch (Exception e) {
            log.info("Error occurred: ", e);
        } finally {
            customWebSocketClient.disconnect();
        }
    }

}
