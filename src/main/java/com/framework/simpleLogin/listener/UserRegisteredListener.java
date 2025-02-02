package com.framework.simpleLogin.listener;

import com.framework.simpleLogin.entity.Email;
import com.framework.simpleLogin.event.UserRegisteredEvent;
import com.framework.simpleLogin.service.EmailService;
import com.framework.simpleLogin.utils.CONSTANT;
import com.framework.simpleLogin.utils.Gadget;
import jakarta.annotation.Resource;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class UserRegisteredListener {
    @Resource
    private EmailService emailService;

    @Async
    @EventListener
    public void handleUserRegisteredEvent(UserRegisteredEvent event) {
        String email = event.getUser().getEmail();
        String username = event.getUser().getUsername();

        Email details = new Email();
        details.setRecipient(email);
        details.setSubject("Registration successful");

        Map<String, Object> variables = new HashMap<>();
        variables.put("username", Gadget.StringUtils.isEmpty(username) ? email : username);

        emailService.sendByTemplate(details, CONSTANT.OTHER.REGISTRATION_SUCCESSFUL_TEMPLATE, variables);
    }
}
