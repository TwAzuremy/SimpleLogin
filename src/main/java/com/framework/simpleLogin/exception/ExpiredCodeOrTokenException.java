package com.framework.simpleLogin.exception;

public class ExpiredCodeOrTokenException extends RuntimeException {
    public ExpiredCodeOrTokenException(String message) {
        super(message);
    }
}
