package com.github.anjeyy.client.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class EntryPoint implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(EntryPoint.class);

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Starting perichat - real time chat application..");
    }
}
