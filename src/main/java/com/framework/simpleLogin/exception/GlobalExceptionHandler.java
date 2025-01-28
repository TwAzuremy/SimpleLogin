package com.framework.simpleLogin.exception;

import com.framework.simpleLogin.utils.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailSendException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(InvalidJwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<Object> handleInvalidJwtException(InvalidJwtException e) {
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED, "The JWT token is invalid.", null);
    }

    @ExceptionHandler(EmptyUserInfoException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleEmptyUserInfoException(EmptyUserInfoException e) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST, "User information cannot be empty.", null);
    }

    @ExceptionHandler({MailSendException.class})
    @ResponseStatus(HttpStatus.GATEWAY_TIMEOUT)
    public ResponseEntity<Object> handleMailConnectException(MailSendException e) {
        logger.warn("Mail send failed.", e);

        return new ResponseEntity<>(HttpStatus.GATEWAY_TIMEOUT, "Mail send failed.", null);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleException(Exception e) {
        logger.warn("An error occurred on the server.", e);

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST, "An error occurred on the server.", null);
    }
}
