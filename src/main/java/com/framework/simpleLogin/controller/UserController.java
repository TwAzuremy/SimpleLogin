package com.framework.simpleLogin.controller;

import com.framework.simpleLogin.dto.UserCaptchaRequest;
import com.framework.simpleLogin.dto.UserResponse;
import com.framework.simpleLogin.entity.User;
import com.framework.simpleLogin.exception.InvalidCaptchaException;
import com.framework.simpleLogin.service.CaptchaService;
import com.framework.simpleLogin.service.UserService;
import com.framework.simpleLogin.utils.*;
import jakarta.annotation.Resource;
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

        return new ResponseEntity<>(HttpStatus.OK, new UserResponse(claims));
    }
}
