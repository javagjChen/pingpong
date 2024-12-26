package org.example;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/pong")
public class PongController {

    private final AtomicInteger requestCount = new AtomicInteger(0);
    private static final int LIMIT = 1;

    @GetMapping("/respond")
    public Mono<String> respond() {
        if (requestCount.incrementAndGet() > LIMIT) {
            return Mono.delay(Duration.ofSeconds(1)) // Ensure the counter resets after a second
                    .then(Mono.error(new PongThrottlingException("Rate limit exceeded")));
        }

        return Mono.just("World")
                .doFinally(signalType -> requestCount.decrementAndGet());
    }

    @ExceptionHandler(PongThrottlingException.class)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public Mono<String> handleThrottling(PongThrottlingException ex) {
        return Mono.just(ex.getMessage());
    }
}


