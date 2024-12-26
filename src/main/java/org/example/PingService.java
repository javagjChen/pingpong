package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.time.Instant;


@Service
public class PingService {

    private static final Logger logger = LoggerFactory.getLogger(PingService.class);
    private static final String PONG_URL = "http://localhost:8080";

    private static final File LOCK_FILE = new File("D:/ping_rate_limit.lock");
    private static final int RQS_LIMIT  = 2;

    private final WebClient webClient = WebClient.create(PONG_URL);


    @Scheduled(fixedRate = 1000)
    public void sendRequest() {
        RandomAccessFile raf = null;
        FileLock lock = null;
        try {
            raf = new RandomAccessFile(LOCK_FILE, "rw");
            lock = raf.getChannel().tryLock();
            //counter
            String content = raf.readLine();
            int counter = 0;
            long lastResetTime = Instant.now().getEpochSecond();
            if (content != null){
                String[] parts = content.split(",");
                counter = Integer.parseInt(parts[0].trim());
                lastResetTime = Long.parseLong(parts[1].trim());
            }

            long currentTime = Instant.now().getEpochSecond();

            // check time is over 1 second
            if (currentTime > lastResetTime) {
                counter = 0;
                lastResetTime = currentTime;
            }
            if (counter < RQS_LIMIT) {
                counter++;
                raf.seek(0);
                raf.setLength(0);
                raf.writeBytes(counter + "," + lastResetTime);
                webClient.post()
                        .uri("/pong")
                        .bodyValue("Hello")
                        .retrieve()
                        .bodyToMono(String.class)
                        .doOnNext(response -> logger.info("Request sent:Hello. Pong responded: " + response))
                        .doOnError(throwable -> logger.info("Request sent but throttled by Pong: " + throwable.getMessage()))
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
                if (raf != null) {
                    raf.close();
                }
            } catch (Exception e) {
                logger.info("Error during cleanup: " + e.getMessage());
            }
        }
    }

    // Reset the counter every second
    @Scheduled(fixedRate = 1000)
    public  void resetCounter() {
        try {
            Thread.sleep(1000);
            RandomAccessFile raf = new RandomAccessFile(LOCK_FILE, "rw");
            FileLock lock = raf.getChannel().lock();
            try {
                long currentTime = Instant.now().getEpochSecond();
                raf.seek(0);
                raf.setLength(0); // clear
                raf.writeBytes("0," + currentTime);
            } finally {
                lock.release();
                raf.close();
            }
        } catch (Exception e) {
            logger.info("Error releasing lock: " + e.getMessage());
        }
    }

}
