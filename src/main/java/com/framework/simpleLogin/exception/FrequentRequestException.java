package com.framework.simpleLogin.exception;

public class FrequentRequestException extends RuntimeException {
    public FrequentRequestException(String message) {
        super(message);
    }
}
