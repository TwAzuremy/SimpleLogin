package com.framework.simpleLogin.listener;

import com.framework.simpleLogin.event.JwtAuthenticationTokenEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationTokenListener {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationTokenListener.class);

    @EventListener
    public void handleJwtAuthenticationToken(JwtAuthenticationTokenEvent event) {
        logger.info(event.getMessage());
    }
}
