package com.framework.simpleLogin.controller;

import com.framework.simpleLogin.dto.UserDTO;
import com.framework.simpleLogin.entity.User;
import com.framework.simpleLogin.service.UserService;
import com.framework.simpleLogin.utils.SimpleUtils;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    @PostMapping("/register")
    public UserDTO register(@RequestBody User user) {
        return new UserDTO(userService.register(user));
    }

    @PostMapping("/login")
    public Object login(@RequestBody User user, @RequestHeader(value = "Authorization", required = false) String token) {
        return SimpleUtils.stringIsEmpty(token) ? userService.login(user) : new UserDTO(userService.getUserFromToken(token.substring(7)));
    }
}
