package com.framework.simpleLogin.advice;

import com.framework.simpleLogin.event.JwtAuthenticationTokenEvent;
import com.framework.simpleLogin.exception.*;
import com.framework.simpleLogin.utils.CONSTANT;
import com.framework.simpleLogin.utils.ResponseEntity;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailSendException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Resource
    private ApplicationEventPublisher eventPublisher;

    @ExceptionHandler(InvalidJwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<Boolean> handleInvalidJwtException(InvalidJwtException e) {
        eventPublisher.publishEvent(
                new JwtAuthenticationTokenEvent(this, e.getMessage())
        );

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED, e.getMessage(), false);
    }

    @ExceptionHandler(AccountLoginLockedException.class)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public ResponseEntity<Boolean> handleAccountLoginLockedException(AccountLoginLockedException e) {
        ResponseEntity<Boolean> response = new ResponseEntity<>(HttpStatus.TOO_MANY_REQUESTS, e.getMessage(), false);
        response.addHeader("Retry-After", String.valueOf(CONSTANT.CACHE_EXPIRATION_TIME.ACCOUNT_LOCKED))
                .addHeader("X-Rate-Limit", String.valueOf(e.getLimit()));

        return response;
    }

    @ExceptionHandler(FrequentRequestException.class)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public ResponseEntity<Boolean> handleFrequentRequestException(FrequentRequestException e) {
        ResponseEntity<Boolean> response = new ResponseEntity<>(HttpStatus.TOO_MANY_REQUESTS, e.getMessage(), false);
        response.addHeader("Retry-After", String.valueOf(CONSTANT.CACHE_EXPIRATION_TIME.API_DEBOUNCE));

        return response;
    }

    @ExceptionHandler(InvalidCaptchaException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<Boolean> handleInvalidCaptchaException(InvalidCaptchaException e) {
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED, "Captcha verification failed.", false);
    }

    @ExceptionHandler(InvalidAccountOrPasswordException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<Boolean> handleInvalidAccountOrPasswordException(InvalidAccountOrPasswordException e) {
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED, "The account or password is incorrect.", false);
    }

    @ExceptionHandler(MailSendException.class)
    @ResponseStatus(HttpStatus.GATEWAY_TIMEOUT)
    public ResponseEntity<Boolean> handleMailConnectException(MailSendException e) {
        logger.warn("Mail send failed.", e);

        return new ResponseEntity<>(HttpStatus.GATEWAY_TIMEOUT, "Mail send failed.", false);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Boolean> handleException(Exception e) {
        logger.warn("An error occurred on the server.", e);

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST, "An error occurred on the server.", false);
    }
}
