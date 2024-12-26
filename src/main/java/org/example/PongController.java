package org.example;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class PongController {

    private static final Logger logger = LoggerFactory.getLogger(PongController.class);

    private final Map<Long, AtomicInteger> requestCounts = new ConcurrentHashMap<>();
    private static final int MAX_REQUESTS_PER_SECOND  = 1;

    @PostMapping("/pong")
    public Mono<ResponseEntity<String>> respond(@RequestBody String message) {
        long currentSecond = System.currentTimeMillis() / 1000;
        requestCounts.computeIfAbsent(currentSecond, k -> new AtomicInteger(0));
        // Clean the old entries to prevent memory leaks
        requestCounts.keySet().removeIf(second -> second < currentSecond - 10);
        if (requestCounts.get(currentSecond).incrementAndGet() <= MAX_REQUESTS_PER_SECOND ) {
            logger.info("Received: " + message + "| Responding: World");
            return Mono.just(ResponseEntity.ok("World"));
        }else {
            logger.info("Received: " + message + " | Throttled: 429 Too Many Requests");
            return Mono.just(ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Rate limit exceeded"));
        }

    }


}


