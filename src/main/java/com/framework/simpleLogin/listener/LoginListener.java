package com.framework.simpleLogin.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class LoginListener {
    private static final Logger logger = LoggerFactory.getLogger(LoginListener.class);

    @EventListener
    public void handleLoginEvent(LoginEvent event) {
        if (event.isSuccess()) {
            logger.info("Login successful: {}", event.getMessage());
        } else {
            logger.info("Login failed: {}", event.getMessage());
        }
    }
}
