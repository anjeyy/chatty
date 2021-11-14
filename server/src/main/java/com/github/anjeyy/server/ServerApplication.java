package com.github.anjeyy.server;

import com.github.anjeyy.common.annotation.ExcludeFromGeneratedJacocoReport;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@ExcludeFromGeneratedJacocoReport
public class ServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class);
    }
}
