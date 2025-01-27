package com.framework.simpleLogin.exception;

import com.framework.simpleLogin.utils.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(InvalidJwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<Object> handleInvalidJwtException(InvalidJwtException e) {
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED, "The JWT token is invalid.", null);
    }

    @ExceptionHandler(NullEmailException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleNullEmailException(NullEmailException e) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST, "Email cannot be null or empty.", null);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleException(Exception e) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST, "An error occurred on the server.", null);
    }
}
