package com.framework.simpleLogin.service;

import com.framework.simpleLogin.entity.User;
import com.framework.simpleLogin.event.SecurityAuthenticationEvent;
import com.framework.simpleLogin.exception.InvalidAccountOrPasswordException;
import com.framework.simpleLogin.repository.UserRepository;
import com.framework.simpleLogin.utils.CONSTANT;
import com.framework.simpleLogin.utils.Encryption;
import com.framework.simpleLogin.utils.RedisUtil;
import jakarta.annotation.Resource;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
public class UserService {
    @Resource
    private UserRepository userRepository;

    @Resource
    private AuthenticationService authenticationService;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private LoginAttemptService loginAttemptService;

    @Resource
    private ApplicationEventPublisher eventPublisher;

    @Transactional
    public void register(User user) {
        if (userRepository.existsUserByEmail(user.getEmail())) {
            throw new RuntimeException("User already exists");
        }

        String salt = Encryption.generateSalt();
        String ciphertext = Encryption.SHA256(user.getPassword() + salt);

        user.setPassword(ciphertext + salt);
        userRepository.save(user);

        redisUtil.delUserCache(user.getEmail());
    }

    public String login(User user) {
        String email = user.getEmail();

        if (loginAttemptService.isLocked(email)) {
            throw new RuntimeException("Login attempt limit exceeded");
        }

        try {
            String token = authenticationService.login(user);

            redisUtil.set(
                    CONSTANT.CACHE_NAME.USER_TOKEN + ":" + email,
                    token,
                    CONSTANT.CACHE_EXPIRATION_TIME.USER_TOKEN,
                    TimeUnit.MILLISECONDS
            );

            loginAttemptService.reset(email);

            eventPublisher.publishEvent(
                    new SecurityAuthenticationEvent(
                            this,
                            "The user '" + email + "' has successfully passed the security authentication and logged in."
                    )
            );

            return token;
        } catch (RuntimeException e) {
            loginAttemptService.failed(email);

            eventPublisher.publishEvent(
                    new SecurityAuthenticationEvent(
                            this,
                            "User '" + email + "' failed security authentication, access denied."
                    )
            );

            throw new InvalidAccountOrPasswordException("The account or password is incorrect.");
        }
    }

    public void logout() {
        String email = authenticationService.logout();
        redisUtil.delUserToken(email);

        eventPublisher.publishEvent(
                new SecurityAuthenticationEvent(
                        this,
                        "The user '" + email + "' has successfully logged out."
                )
        );
    }
}
