package com.framework.simpleLogin.exception;

import lombok.Getter;

@Getter
public class AccountLoginLockedException extends RuntimeException {
    private final int limit;

    public AccountLoginLockedException(String message, int limit) {
        super(message);

        this.limit = limit;
    }

}
