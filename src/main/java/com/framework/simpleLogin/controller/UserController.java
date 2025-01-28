package com.framework.simpleLogin.controller;

import com.framework.simpleLogin.dto.UserDTO;
import com.framework.simpleLogin.entity.User;
import com.framework.simpleLogin.service.LoginAttemptService;
import com.framework.simpleLogin.service.UserService;
import com.framework.simpleLogin.utils.ResponseEntity;
import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    @Resource
    private LoginAttemptService loginAttemptService;

    @PostMapping("/register")
    public ResponseEntity<Boolean> register(@RequestBody User user) {
        return new ResponseEntity<>(HttpStatus.OK, userService.register(user));
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody User user) {
        if (loginAttemptService.isLocked(user.getEmail())) {
            return new ResponseEntity<>(HttpStatus.TOO_MANY_REQUESTS, "Too many failed logins.");
        }

        Map<String, Object> result = userService.login(user);

        if (result != null) {
            loginAttemptService.reset(user.getEmail());
        }

        return new ResponseEntity<>(HttpStatus.OK, result);
    }

    @PostMapping("/verify")
    public ResponseEntity<UserDTO> verify(@RequestHeader(value = "Authorization", required = false) String token) {
        return new ResponseEntity<>(HttpStatus.OK, new UserDTO(userService.verifyByToken(token)));
    }
}
