package com.framework.simpleLogin.controller;

import com.framework.simpleLogin.annotation.Debounce;
import com.framework.simpleLogin.dto.EmailRequest;
import com.framework.simpleLogin.service.EmailService;
import com.framework.simpleLogin.utils.CONSTANT;
import com.framework.simpleLogin.utils.Gadget;
import com.framework.simpleLogin.utils.JwtUtil;
import com.framework.simpleLogin.utils.ResponseEntity;
import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/email")
public class EmailController {
    @Resource
    private EmailService emailService;

    /**
     * Sends an email with a template and returns a response indicating whether the email was sent successfully.
     * <p></p>
     * If the provided username is empty, it defaults to the email address.
     *
     * @param email    the recipient's email address
     * @param subject  the email subject
     * @param content  the email content
     * @param username the username to be used in the email template (optional)
     * @param cacheName the name of the cache where the captcha is stored
     * @return a ResponseEntity containing a boolean indicating whether the email was sent successfully
     */
    private ResponseEntity<Boolean> sendTemplate(String email, String subject, String content, String username, String cacheName) {
        if (Gadget.StringUtils.isEmpty(username)) {
            username = email;
        }

        Map<String, Object> variables = new HashMap<>();
        variables.put("username", username);
        variables.put("content", content);

        try {
            boolean isSend = emailService.sendCaptcha(cacheName, email, subject, variables).get();

            return new ResponseEntity<>(HttpStatus.OK, isSend);
        } catch (InterruptedException | ExecutionException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST, false);
        }
    }

    @Debounce(cacheName = CONSTANT.CACHE_NAME.API_DEBOUNCE + ":register", key = "#request")
    @PostMapping("/send-register-captcha")
    public ResponseEntity<Boolean> sendRegisterCaptcha(@RequestBody EmailRequest request) {
        return this.sendTemplate(request.getEmail(), "Register",
                "To confirm this email for your account, enter the following verification code in the app: (Valid for 30 minutes)",
                request.getUsername(),
                CONSTANT.CACHE_NAME.CAPTCHA_REGISTER);
    }

    @Debounce(cacheName = CONSTANT.CACHE_NAME.API_DEBOUNCE + ":modify-password", key = "#request")
    @PostMapping("/send-modify-password-captcha")
    public ResponseEntity<Boolean> sendModifyPasswordCaptcha(@RequestBody EmailRequest request,
                                                             @RequestHeader(value = "Authorization") String token) {
        String email = (String) JwtUtil.parse(Gadget.requestTokenProcessing(token)).get("email");

        return this.sendTemplate(email, "Modify Password",
                "To modify your password, enter the following verification code in the app: (Valid for 30 minutes)",
                request.getUsername(),
                CONSTANT.CACHE_NAME.CAPTCHA_MODIFY_PASSWORD);
    }

    @Debounce(cacheName = CONSTANT.CACHE_NAME.API_DEBOUNCE + ":reset-password", key = "#request")
    @PostMapping("/send-reset-password-captcha")
    public ResponseEntity<Boolean> sendResetPasswordCaptcha(@RequestBody EmailRequest request) {
        return this.sendTemplate(request.getEmail(), "Reset Password",
                "To reset your password, enter the following verification code in the app: (Valid for 30 minutes)",
                null,
                CONSTANT.CACHE_NAME.CAPTCHA_RESET_PASSWORD);
    }
}
