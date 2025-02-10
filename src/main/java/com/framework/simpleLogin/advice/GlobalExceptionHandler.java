package com.framework.simpleLogin.advice;

import com.framework.simpleLogin.exception.*;
import com.framework.simpleLogin.utils.CONSTANT;
import com.framework.simpleLogin.utils.Gadget;
import com.framework.simpleLogin.utils.ResponseEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailSendException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    private void ContainsUserLog(String message, String email) {
        if (!Gadget.StringUtils.isEmpty(email)) {
            log.info("[{}] User username: {}", message, email);
        }
    }

    @ExceptionHandler(AccountLoginLockedException.class)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public ResponseEntity<Boolean> handleAccountLoginLockedException(AccountLoginLockedException e) {
        ResponseEntity<Boolean> response = new ResponseEntity<>(HttpStatus.TOO_MANY_REQUESTS, e.getMessage(), false);
        response.addHeader("Retry-After", String.valueOf(CONSTANT.CACHE_EXPIRATION_TIME.ACCOUNT_LOCKED))
                .addHeader("X-Rate-Limit", String.valueOf(e.getLimit()));

        return response;
    }

    @ExceptionHandler(ExistsUserException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Boolean> handleExistsUserException(ExistsUserException e) {
        return new ResponseEntity<>(HttpStatus.CONFLICT, e.getMessage(), false);
    }

    @ExceptionHandler(FrequentRequestException.class)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public ResponseEntity<Boolean> handleFrequentRequestException(FrequentRequestException e) {
        ResponseEntity<Boolean> response = new ResponseEntity<>(HttpStatus.TOO_MANY_REQUESTS, e.getMessage(), false);
        response.addHeader("Retry-After", String.valueOf(CONSTANT.CACHE_EXPIRATION_TIME.API_DEBOUNCE));

        return response;
    }

    @ExceptionHandler(InvalidAccountOrPasswordException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<Boolean> handleInvalidAccountOrPasswordException(InvalidAccountOrPasswordException e) {
        this.ContainsUserLog(e.getMessage(), e.getEmail());

        ResponseEntity<Boolean> response = new ResponseEntity<>(HttpStatus.UNAUTHORIZED, e.getMessage(), false);
        response.addHeader("X-Rate-Limit", String.valueOf(e.getLimit()));

        return response;
    }

    @ExceptionHandler(InvalidCaptchaException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<Boolean> handleInvalidCaptchaException(InvalidCaptchaException e) {
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED, "Captcha verification failed.", false);
    }

    @ExceptionHandler(InvalidJwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<Boolean> handleInvalidJwtException(InvalidJwtException e) {
        this.ContainsUserLog(e.getMessage(), e.getEmail());

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED, e.getMessage(), false);
    }

    @ExceptionHandler(InvalidSignatureException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<Boolean> handleInvalidSignatureException(InvalidSignatureException e) {
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED, e.getMessage(), false);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Boolean> handleMissingRequestHeaderException(MissingRequestHeaderException e) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST, e.getMessage(), false);
    }

    @ExceptionHandler(MissingUserException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Boolean> handleMissingUserException(MissingUserException e) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST, e.getMessage(), false);
    }

    @ExceptionHandler(SamePasswordException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Boolean> handleSamePasswordException(SamePasswordException e) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST, e.getMessage(), false);
    }

    @ExceptionHandler(UnableConnectServerException.class)
    @ResponseStatus(HttpStatus.GATEWAY_TIMEOUT)
    public ResponseEntity<Boolean> handleUnableConnectServerException(UnableConnectServerException e) {
        log.warn("Unable connect server.", e);

        return new ResponseEntity<>(HttpStatus.GATEWAY_TIMEOUT, e.getMessage(), false);
    }

    @ExceptionHandler(MailSendException.class)
    @ResponseStatus(HttpStatus.GATEWAY_TIMEOUT)
    public ResponseEntity<Boolean> handleMailConnectException(MailSendException e) {
        log.warn("Mail send failed.", e);

        return new ResponseEntity<>(HttpStatus.GATEWAY_TIMEOUT, "Mail send failed.", false);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Boolean> handleException(Exception e) {
        log.warn("An error occurred on the server.", e);

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST, "An error occurred on the server.", false);
    }
}
