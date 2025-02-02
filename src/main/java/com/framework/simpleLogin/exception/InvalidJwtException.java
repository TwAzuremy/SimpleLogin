package com.framework.simpleLogin.exception;

import lombok.Getter;

@Getter
public class InvalidJwtException extends RuntimeException {
    private final String email;

    public InvalidJwtException(String message, String email) {
        super(message);

        this.email = email;
    }
}
