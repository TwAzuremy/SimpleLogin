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

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private EmailService emailService;

    @PostMapping("/sendText")
    public String sendText(@RequestBody Email details) {
        return emailService.sendMail(details, true);
    }

    @PostMapping("/sendTemplate")
    public String sendTemplate(@RequestBody Email details) {
        Map<String, Object> variables = new HashMap<>();

        variables.put("code", 114514);

        return emailService.sendTemplateMail(details, variables);
    }
}
