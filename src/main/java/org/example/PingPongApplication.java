package org.example;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PingPongApplication {
    public static void main(String[] args) {
        SpringApplication.run(PingPongApplication.class, args);
    }
}