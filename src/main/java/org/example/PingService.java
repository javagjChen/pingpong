package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;


@Component
public class PingService {

    private static final String PONG_URL = "http://localhost:8080/pong/respond";
    private final WebClient webClient = WebClient.create(PONG_URL);
    private static final Logger logger = LoggerFactory.getLogger(PingService.class);

    @Scheduled(fixedRate = 1000)
    public void sendRequest() {
        File lockFile = new File("D:/ping_rate_limit.lock");
        RandomAccessFile randomAccessFile = null;
        FileLock lock = null;
        try {
            randomAccessFile = new RandomAccessFile(lockFile, "rw");
            lock = randomAccessFile.getChannel().tryLock();
            if (lock != null) {
                webClient.get()
                        .retrieve()
                        .bodyToMono(String.class)
                        .doOnNext(response -> logger.info("Request sent. Pong responded: " + response))
                        .doOnError(throwable -> logger.info("Request throttled by Pong: " + throwable.getMessage()))
                        .subscribe();
            } else {
                logger.info("Request not sent as rate limited");
            }

        } catch (Exception e) {
            logger.info("Error during rate limiting: " + e.getMessage());
        }finally {
            try {
                if (lock != null) {
                    lock.release();
                }
                if (randomAccessFile != null) {
                    randomAccessFile.close();
                }
            } catch (Exception e) {
                logger.info("Error during cleanup: " + e.getMessage());
            }
        }
    }


}
