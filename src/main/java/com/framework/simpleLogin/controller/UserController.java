package com.framework.simpleLogin.controller;

import com.framework.simpleLogin.dto.UserCaptchaRequest;
import com.framework.simpleLogin.dto.UserResponse;
import com.framework.simpleLogin.entity.User;
import com.framework.simpleLogin.event.UserRegisteredEvent;
import com.framework.simpleLogin.exception.InvalidCaptchaException;
import com.framework.simpleLogin.service.CaptchaService;
import com.framework.simpleLogin.service.UserService;
import com.framework.simpleLogin.utils.*;
import jakarta.annotation.Resource;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    @Resource
    private UserService userService;

    @Resource
    private CaptchaService captchaService;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private ApplicationEventPublisher eventPublisher;

    /*
     * Use the "/send-register-captcha" API in 'EmailController' to send the captcha,
     * and then send the received captcha with the user's information.
     */
    @PostMapping("/register")
    public ResponseEntity<Boolean> register(@RequestBody UserCaptchaRequest userCaptchaRequest) {
        User user = userCaptchaRequest.getUser();
        String captcha = userCaptchaRequest.getCaptcha();

        if (!captchaService.verify(CONSTANT.CACHE_NAME.CAPTCHA_REGISTER, captcha, user.getEmail())) {
            throw new InvalidCaptchaException("Captcha verification failed.");
        }

        userService.register(user);
        redisUtil.delCaptchaRegister(user.getEmail());

        eventPublisher.publishEvent(new UserRegisteredEvent(this, new UserResponse(user)));
        return new ResponseEntity<>(HttpStatus.CREATED, "User registered successfully.", true);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user) {
        return new ResponseEntity<>(HttpStatus.OK, userService.login(user));
    }

    @PostMapping("/logout")
    public ResponseEntity<Boolean> logout() {
        userService.logout();

        return new ResponseEntity<>(HttpStatus.NO_CONTENT, "User logged out successfully.", true);
    }

    @GetMapping("/get-info")
    public ResponseEntity<UserResponse> getInfo(@RequestHeader(value = "Authorization") String token) {
        Map<String, Object> claims = JwtUtil.parse(Gadget.requestTokenProcessing(token));
        long id = (long) claims.get("id");

        return new ResponseEntity<>(HttpStatus.OK, userService.getInfo(id));
    }

    @PatchMapping("/reset-password")
    public ResponseEntity<Number> resetPassword(
            @RequestBody UserCaptchaRequest userCaptchaRequest,
            @RequestHeader(value = "Authorization") String token) {

        Map<String, Object> claims = JwtUtil.parse(Gadget.requestTokenProcessing(token));

        if (!captchaService.verify(
                CONSTANT.CACHE_NAME.CAPTCHA_RESET_PASSWORD,
                userCaptchaRequest.getCaptcha(),
                (String) claims.get("email")
        )) {
            throw new InvalidCaptchaException("Captcha verification failed.");
        }

        if (userCaptchaRequest.getAttachment() instanceof Map<?, ?> attachment) {
            return new ResponseEntity<>(HttpStatus.OK, userService.resetPassword(
                    (long) claims.get("id"),
                    (String) attachment.get("oldPassword"),
                    (String) attachment.get("newPassword")));
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST, -1);
    }
}