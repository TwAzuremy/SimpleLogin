package com.framework.simpleLogin.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
@Setter
public class ResponseEntity<T> {
    private HttpStatus status;
    private String message;
    private T data;

    public ResponseEntity(HttpStatus status, T data) {
        this.status = status;
        this.message = status.getReasonPhrase();
        this.data = data;
    }
}
