package org.example.ping;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Collections;

@SpringBootApplication
@EnableScheduling
public class PingApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(PingApplication.class);
        app.setDefaultProperties(Collections.singletonMap("server.port", "8085"));
        app.run(args);
    }
}