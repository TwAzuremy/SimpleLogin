package com.framework.simpleLogin.service;

import com.framework.simpleLogin.dto.UserLoginRequest;
import com.framework.simpleLogin.dto.UserResponse;
import com.framework.simpleLogin.entity.OAuthUser;
import com.framework.simpleLogin.entity.User;
import com.framework.simpleLogin.exception.*;
import com.framework.simpleLogin.repository.OAuthUserRepository;
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
import java.util.Optional;

@Slf4j
@Service
public class UserService {
    @Resource
    private UserRepository userRepository;

    @Resource
    private OAuthUserRepository oAuthUserRepository;

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

        // If the username is empty, replace it with an email address.
        if (Gadget.StringUtils.isEmpty(user.getUsername())) {
            user.setUsername(user.getEmail());
        }

        String salt = Encryption.generateSalt();
        String ciphertext = Encryption.SHA256(user.getPassword() + salt);

        user.setPassword(ciphertext + salt);
        userRepository.save(user);

        redisUtil.delUserCache(user.getEmail());
        log.info("[User registered] User email: {}", user.getEmail());
    }

    public String login(UserLoginRequest user) {
        String username = user.getUsername();

        if (loginAttemptService.isLocked(username)) {
            throw new AccountLoginLockedException("Login attempt limit exceeded", loginAttemptService.getAttempts(username));
        }

        try {
            String token = authenticationService.login(user);

            // TODO 修改缓存键: 待定
            redisUtil.set(
                    CONSTANT.CACHE_NAME.USER_TOKEN + ":" + username,
                    token,
                    CONSTANT.CACHE_EXPIRATION_TIME.USER_TOKEN
            );

            loginAttemptService.reset(username);
            log.info("[Security verification successful] User email: {}", username);

            return token;
        } catch (RuntimeException e) {
            loginAttemptService.failed(username);
            log.info("[Security verification failed] User email: {}", username);

            Object limit = redisUtil.get(CONSTANT.CACHE_NAME.USER_ATTEMPT + ":" + username);

            throw new InvalidAccountOrPasswordException(
                    "The account or password is incorrect", username,
                    limit == null ? 1 : Integer.parseInt(limit.toString()));
        }
    }

    @Transactional
    public User findOrCreateUser(OAuthUser oAuthUser) {
        Optional<OAuthUser> dbOAuthUser = oAuthUserRepository.findByProviderAndProviderId(
                oAuthUser.getProvider(), oAuthUser.getProviderId()
        );

        if (dbOAuthUser.isPresent()) {
            return dbOAuthUser.get().getUser();
        }

        User user = oAuthUser.getUser();
        userRepository.save(user);
        oAuthUserRepository.save(oAuthUser);

        return oAuthUser.getUser();
    }

    public void logout() {
        String email = authenticationService.logout();
        redisUtil.delUserToken(email);

        log.info("[User logout] User email: {}", email);
    }

    public UserResponse getInfo(long id) {
        return userRepository.findUserExcludePasswordById(id).orElseThrow(() -> new MissingUserException("User not found"));
    }

    @Transactional
    public long resetPassword(long id, String oldPassword, String newPassword) {
        User dbUser = userRepository.findUserById(id).orElseThrow(() -> new MissingUserException("User not found"));
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
