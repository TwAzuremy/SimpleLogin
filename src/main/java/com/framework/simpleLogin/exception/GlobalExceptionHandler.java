package com.framework.simpleLogin.exception;

import com.framework.simpleLogin.event.JwtAuthenticationTokenEvent;
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
