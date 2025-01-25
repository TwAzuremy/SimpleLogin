package com.framework.simpleLogin.controller;

import com.framework.simpleLogin.dto.UserDTO;
import com.framework.simpleLogin.entity.User;
import com.framework.simpleLogin.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    @PostMapping("/register")
    public UserDTO register(@RequestBody User user) {
        User dbUser = userService.register(user);

        if (dbUser != null) {
            return new UserDTO(dbUser);
        }

        return null;
    }

    @PostMapping("/login")
    public UserDTO login(@RequestBody User user) {
        User dbUser = userService.login(user);

        if (dbUser != null) {
            return new UserDTO(dbUser);
        }

        return null;
    }
}
