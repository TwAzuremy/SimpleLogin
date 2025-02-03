package com.framework.simpleLogin.exception;

import lombok.Getter;

@Getter
public class InvalidAccountOrPasswordException extends RuntimeException {
    private final String email;
    private final int limit;

    public InvalidAccountOrPasswordException(String message, String email, int limit) {
        super(message);

        this.email = email;
        this.limit = limit;
    }
}
