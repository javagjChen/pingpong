package org.example;

class PongThrottlingException extends RuntimeException {
    public PongThrottlingException(String message) {
        super(message);
    }
}
