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

    @Debounce(cacheName = CONSTANT.CACHE_NAME.API_DEBOUNCE + ":register", key = "#request")
    @PostMapping("/send-register-captcha")
    public ResponseEntity<Boolean> sendRegisterCaptcha(@RequestBody EmailRequest request) {
        String email = request.getEmail();
        String username = request.getUsername();

        Map<String, Object> variables = new HashMap<>();
        variables.put("username", Gadget.StringUtils.isEmpty(username) ? email : username);
        variables.put("content", "To confirm this email for your account, " +
                "enter the following verification code in the app: (Valid for 30 minutes)");

        try {
            boolean isSend = emailService.sendCaptcha(CONSTANT.CACHE_NAME.CAPTCHA_REGISTER, email,
                    "Register", variables).get();

            return new ResponseEntity<>(HttpStatus.OK, isSend);
        } catch (InterruptedException | ExecutionException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST, false);
        }
    }

    @Debounce(cacheName = CONSTANT.CACHE_NAME.API_DEBOUNCE + ":reset-password", key = "#request")
    @PostMapping("/send-reset-password-captcha")
    public ResponseEntity<Boolean> sendResetPasswordCaptcha(@RequestBody EmailRequest request,
                                                            @RequestHeader(value = "Authorization") String token) {
        String email = (String) JwtUtil.parse(Gadget.requestTokenProcessing(token)).get("email");
        String username = request.getUsername();

        Map<String, Object> variables = new HashMap<>();
        variables.put("username", Gadget.StringUtils.isEmpty(username) ? email : username);
        variables.put("content", "To reset your password, enter the following verification code in the app: (Valid for 30 minutes)");

        try {
            boolean isSend = emailService.sendCaptcha(CONSTANT.CACHE_NAME.CAPTCHA_RESET_PASSWORD, email,
                    "Reset Password", variables).get();

            return new ResponseEntity<>(HttpStatus.OK, isSend);
        } catch (InterruptedException | ExecutionException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST, false);
        }
    }
}
