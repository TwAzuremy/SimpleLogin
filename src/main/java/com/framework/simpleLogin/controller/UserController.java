package com.framework.simpleLogin.controller;

import com.framework.simpleLogin.mail.Email;
import com.framework.simpleLogin.service.EmailService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private EmailService emailService;

    @PostMapping("/sendText")
    public Boolean sendText(@RequestBody Email details) {
        return emailService.sendMail(details, true);
    }

    @PostMapping("/sendTemplate")
    public Boolean sendTemplate(@RequestBody Email details) {
        Map<String, Object> variables = new HashMap<>();

        variables.put("username", details.getRecipient());
        variables.put("content", "To confirm this email for your account, enter the following verification code in the app: ");

        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random rand = new Random();

        String code = rand.ints(6, 0, chars.length())
                .mapToObj(i -> String.valueOf(chars.charAt(i)))
                .collect(Collectors.joining());
        variables.put("code", code);

        return emailService.sendTemplateMail(details, variables);
    }
}
