package com.framework.simpleLogin.controller;

import com.framework.simpleLogin.annotation.Debounce;
import com.framework.simpleLogin.entity.Email;
import com.framework.simpleLogin.service.CaptchaService;
import com.framework.simpleLogin.service.EmailService;
import com.framework.simpleLogin.utils.CONSTANT;
import com.framework.simpleLogin.utils.Gadget;
import com.framework.simpleLogin.utils.ResponseEntity;
import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/email")
public class EmailController {
    @Resource
    private CaptchaService captchaService;

    @Resource
    private EmailService emailService;

    @Debounce(key = "#email")
    @PostMapping("/send-register-captcha")
    public ResponseEntity<Boolean> sendRegisterCaptcha(@RequestBody String email) {
        String captcha = captchaService.get(CONSTANT.CACHE_NAME.CAPTCHA_REGISTER, email);

        if (Gadget.StringUtils.isEmpty(captcha)) {
            captcha = captchaService.generate(6);
            captchaService.store(CONSTANT.CACHE_NAME.CAPTCHA_REGISTER, email, captcha);
        }

        String content = "To confirm this email for you account, enter the following captcha in the app:";
        Map<String, Object> variables = Map.of("username", email, "content", content, "code", captcha);

        Email details = new Email();
        details.setRecipient(email);
        details.setSubject("Register Captcha");

        try {
            boolean isSend = emailService.sendByTemplate(details, "mail/CaptchaHTML.html", variables).get();

            return new ResponseEntity<>(HttpStatus.OK, isSend);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
