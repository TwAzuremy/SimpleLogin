package com.framework.simpleLogin.exception;

public class MissingUserException extends RuntimeException {
    public MissingUserException(String message) {
        super(message);
    }
}
