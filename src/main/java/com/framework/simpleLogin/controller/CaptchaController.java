package com.framework.simpleLogin.controller;

import com.framework.simpleLogin.mail.Email;
import com.framework.simpleLogin.service.CaptchaService;
import com.framework.simpleLogin.service.EmailService;
import com.framework.simpleLogin.utils.SimpleUtils;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/captcha")
public class CaptchaController {
    @Resource
    private EmailService emailService;

    @Resource
    private CaptchaService captchaService;

    @GetMapping("/send")
    public Boolean send(String recipient) {
        String captcha = captchaService.isExists(recipient);

        if (SimpleUtils.stringIsEmpty(captcha)) {
            captcha = captchaService.generator(6);

            // Store the captcha in Redis
            captchaService.store(recipient, captcha);
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
            return false;
        }
    }

    @PostMapping("/verify")
    public Boolean verify(@RequestParam String recipient, @RequestParam String captcha) {
        return captchaService.verify(recipient, captcha);
    }
}
