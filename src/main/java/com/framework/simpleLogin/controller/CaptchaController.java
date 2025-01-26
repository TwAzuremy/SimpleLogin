package com.framework.simpleLogin.controller;

import com.framework.simpleLogin.mail.Email;
import com.framework.simpleLogin.service.CaptchaService;
import com.framework.simpleLogin.service.EmailService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/captcha")
public class CaptchaController {
    @Resource
    private EmailService emailService;

    @Resource
    private CaptchaService captchaService;

    private final Logger logger = LoggerFactory.getLogger(CaptchaController.class);

    @GetMapping("/send")
    public Boolean send(String recipient) {
        String captcha = captchaService.isExists(recipient);

        if (captcha == null) {
            captcha = captchaService.generator(6);

            // Store the captcha in Redis
            long timeout = 10;
            captchaService.store(recipient, captcha, timeout, TimeUnit.MINUTES);
        }

        Map<String, Object> variables = new HashMap<>();
        variables.put("username", recipient);
        variables.put("content", "To confirm this email for your account, enter the following verification code in the app: ");
        variables.put("code", captcha);

        Email details = new Email();
        details.setRecipient(recipient);
        details.setSubject("Confirm captcha");

        try {
            return emailService.sendTemplateMail(details,
                    "mail/CaptchaHTML.html",
                    variables,
                    "The verification code '" + captcha + "' has been sent to the mailbox: " + recipient).get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Failed to send an email to: {}.\nError message: {}", recipient, e.getMessage());

            return false;
        }
    }

    @PostMapping("/verify")
    public Boolean verify(@RequestParam String recipient, @RequestParam String captcha) {
        return captchaService.verify(recipient, captcha);
    }
}
