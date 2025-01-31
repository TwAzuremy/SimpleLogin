package com.framework.simpleLogin.exception;

public class InvalidAccountOrPasswordException extends RuntimeException {
    public InvalidAccountOrPasswordException(String message) {
        super(message);
    }
}
