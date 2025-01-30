package com.framework.simpleLogin.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class JpaValidationListener {
    private static final Logger logger = LoggerFactory.getLogger(JpaValidationListener.class);

    @EventListener
    public void handleJpaValidationEvent(JpaValidationEvent event) {
        if (event.isSuccess()) {
            logger.info("JPA verification successful: {}", event.getMessage());
        } else {
            logger.info("JPA verification failed: {}", event.getMessage());
        }
    }
}
