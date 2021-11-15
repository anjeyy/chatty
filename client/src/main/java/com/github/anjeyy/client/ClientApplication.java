package com.github.anjeyy.client;

import com.github.anjeyy.client.infrastructure.properties.WebSocketProperties;
import com.github.anjeyy.common.annotation.ExcludeFromGeneratedJacocoReport;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@ExcludeFromGeneratedJacocoReport
@EnableConfigurationProperties({WebSocketProperties.class})
public class ClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class);
    }
}
