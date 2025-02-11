package com.framework.simpleLogin.exception;

import lombok.Getter;

@Getter
public class InvalidJwtException extends RuntimeException {
    private final String id;

    public InvalidJwtException(String message, String id) {
        super(message);

        this.id = id;
    }
}
