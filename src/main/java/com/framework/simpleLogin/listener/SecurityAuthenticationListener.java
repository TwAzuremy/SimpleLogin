package com.framework.simpleLogin.listener;

import com.framework.simpleLogin.event.SecurityAuthenticationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class SecurityAuthenticationListener {
    private static final Logger logger = LoggerFactory.getLogger(SecurityAuthenticationListener.class);

    @EventListener
    public void handleSecurityAuthentication(SecurityAuthenticationEvent event) {
        logger.info(event.getMessage());
    }
}
