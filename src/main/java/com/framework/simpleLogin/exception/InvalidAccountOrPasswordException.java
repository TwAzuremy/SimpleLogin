package com.framework.simpleLogin.exception;

import lombok.Getter;

@Getter
public class InvalidAccountOrPasswordException extends RuntimeException {
    private final String email;

    public InvalidAccountOrPasswordException(String message, String email) {
        super(message);

        this.email = email;
    }
}
