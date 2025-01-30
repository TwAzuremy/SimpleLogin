package com.framework.simpleLogin.controller;

import com.framework.simpleLogin.dto.UserDTO;
import com.framework.simpleLogin.entity.User;
import com.framework.simpleLogin.listener.LoginEvent;
import com.framework.simpleLogin.service.LoginAttemptService;
import com.framework.simpleLogin.service.UserService;
import com.framework.simpleLogin.utils.ResponseEntity;
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
    private LoginAttemptService loginAttemptService;

    @Resource
    private ApplicationEventPublisher eventPublisher;

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

            eventPublisher.publishEvent(
                    new LoginEvent(this, "User '" + user.getEmail() + "' logged in.", true)
            );

            return new ResponseEntity<>(HttpStatus.OK, result);
        }

        eventPublisher.publishEvent(
                new LoginEvent(this, "User '" + user.getEmail() + "' failed to log in.", false)
        );

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED, "The username or password is incorrect.", false);
    }

    @GetMapping("/verify-token")
    public ResponseEntity<UserDTO> verify(@RequestHeader(value = "Authorization", required = false) String token) {
        UserDTO dto = new UserDTO(userService.verifyByToken(token));

        eventPublisher.publishEvent(
                new LoginEvent(this, "User '" + dto.getEmail() + "' logged in.", true)
        );

        return new ResponseEntity<>(HttpStatus.OK, dto);
    }

    @PatchMapping("/reset-password")
    public ResponseEntity<Boolean> resetPassword(@RequestBody User user, @RequestHeader(value = "Authorization", required = false) String token) {
        Map<String, Object> result = userService.verifyByToken(token);
        User tokenUser = new User() {
            {
                setId((int) result.get("id"));
                setUsername((String) result.get("username"));
                setEmail((String) result.get("email"));
            }
        };

        if (!tokenUser.equals(user)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST, "User information does not match.", false);
        }

        userService.resetPassword(user);

        return new ResponseEntity<>(HttpStatus.OK, true);
    }

    @PatchMapping("/reset-email")
    public ResponseEntity<Boolean> resetEmail(@RequestBody User user, @RequestHeader(value = "Authorization", required = false) String token) {
        if (!userService.verifyByTokenAndId(token, user.getId())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST, "User information does not match.", false);
        }

        if (userService.verifyByToken(token).get("email").equals(user.getEmail())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST, "Consistent with the original data.", false);
        }

        userService.resetEmail(user);

        return new ResponseEntity<>(HttpStatus.OK, true);
    }

    @PatchMapping("/reset-other-settings")
    public ResponseEntity<Object> resetOtherSettings(@RequestBody User user, @RequestHeader(value = "Authorization", required = false) String token) {
        if (!userService.verifyByTokenAndId(token, user.getId())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST, "User information does not match.", null);
        }

        return new ResponseEntity<>(HttpStatus.OK, null);
    }
}
