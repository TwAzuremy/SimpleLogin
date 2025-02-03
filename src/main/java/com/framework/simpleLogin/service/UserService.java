package com.framework.simpleLogin.service;

import com.framework.simpleLogin.dto.UserResponse;
import com.framework.simpleLogin.entity.User;
import com.framework.simpleLogin.exception.AccountLoginLockedException;
import com.framework.simpleLogin.exception.ExistsUserException;
import com.framework.simpleLogin.exception.InvalidAccountOrPasswordException;
import com.framework.simpleLogin.exception.SamePasswordException;
import com.framework.simpleLogin.repository.UserRepository;
import com.framework.simpleLogin.utils.CONSTANT;
import com.framework.simpleLogin.utils.Encryption;
import com.framework.simpleLogin.utils.Gadget;
import com.framework.simpleLogin.utils.RedisUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
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

    @Transactional
    public void register(User user) {
        if (userRepository.existsUserByEmail(user.getEmail())) {
            throw new ExistsUserException("User already exists");
        }

        String salt = Encryption.generateSalt();
        String ciphertext = Encryption.SHA256(user.getPassword() + salt);

        user.setPassword(ciphertext + salt);
        userRepository.save(user);

        redisUtil.delUserCache(user.getEmail());
        log.info("[User registered] User email: {}", user.getEmail());
    }

    public String login(User user) {
        String email = user.getEmail();

        if (loginAttemptService.isLocked(email)) {
            throw new AccountLoginLockedException("Login attempt limit exceeded", loginAttemptService.getAttempts(email));
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
            log.info("[Security verification successful] User email: {}", email);

            return token;
        } catch (RuntimeException e) {
            loginAttemptService.failed(email);
            log.info("[Security verification failed] User email: {}", email);

            Object limit = redisUtil.get(CONSTANT.CACHE_NAME.USER_ATTEMPT + ":" + email);

            throw new InvalidAccountOrPasswordException(
                    "The account or password is incorrect", email, limit == null ? 1 : Integer.parseInt(limit.toString()));
        }
    }

    public void logout() {
        String email = authenticationService.logout();
        redisUtil.delUserToken(email);

        log.info("[User logout] User email: {}", email);
    }

    public UserResponse getInfo(int id) {
        return userRepository.findUserExcludePasswordById(id);
    }

    @Transactional
    public int resetPassword(int id, String oldPassword, String newPassword) {
        User dbUser = userRepository.findUserById(id);
        Map<String, String> separate = Gadget.StringUtils.separateCiphertext(dbUser.getPassword());

        String oldPasswordCiphertext = Encryption.SHA256(oldPassword + separate.get("salt"));

        // Verify that the password is correct.
        if (!oldPasswordCiphertext.equals(separate.get("ciphertext"))) {
            throw new InvalidAccountOrPasswordException("The password is incorrect", dbUser.getEmail(), 0);
        }

        // Check whether the old password is inconsistent with the new password.
        if (oldPassword.equals(newPassword)) {
            throw new SamePasswordException("The new password cannot be the same as the old password");
        }

        // Encrypt the new password, and perform the modification operation.
        String salt = Encryption.generateSalt();
        String ciphertext = Encryption.SHA256(newPassword + salt);

        return userRepository.updatePasswordById(ciphertext + salt, id);
    }
}
