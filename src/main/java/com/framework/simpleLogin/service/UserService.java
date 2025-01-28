package com.framework.simpleLogin.service;

import com.framework.simpleLogin.dto.UserDTO;
import com.framework.simpleLogin.entity.User;
import com.framework.simpleLogin.exception.NullUserException;
import com.framework.simpleLogin.exception.SamePasswordException;
import com.framework.simpleLogin.repository.UserRepository;
import com.framework.simpleLogin.utils.CACHE_NAME;
import com.framework.simpleLogin.utils.Encryption;
import com.framework.simpleLogin.utils.JwtUtils;
import com.framework.simpleLogin.utils.SimpleUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class UserService {
    @Resource
    private UserRepository userRepository;

    @Resource
    private RedisService redisService;

    @Resource
    private LoginAttemptService loginAttemptService;

    @Resource
    private JwtUtils jwtUtils;

    public boolean register(User user) {
        if (userRepository.existsUserByEmail(user.getEmail())) {
            return false;
        }

        if (SimpleUtils.stringIsEmpty(user.getUsername())) {
            user.setUsername(user.getEmail());
        }

        userRepository.save(user.encryption(null));

        redisService.delete(CACHE_NAME.USER + ":cache:" + user.getEmail());

        return true;
    }

    public Map<String, Object> login(User user) {
        User dbUser = userRepository.findByEmail(user.getEmail());

        if (dbUser == null) {
            throw new NullUserException("The user does not exist.");
        }

        // Compare the two ciphertexts.
        if (dbUser.equalsPassword(user.getPassword())) {
            return new HashMap<>() {
                {
                    put("user", new UserDTO(dbUser));
                    put("token", jwtUtils.generateTokenForUser(new UserDTO(dbUser)));
                }
            };
        }

        loginAttemptService.failed(user.getEmail());

        return null;
    }

    public Map<String, Object> verifyByToken(String token) {
        return jwtUtils.getClaims(token.substring(SimpleUtils.authorizationPrefix.length()));
    }

    public void resetPassword(User user) {
        User dbUser = userRepository.findByEmail(user.getEmail());

        if (dbUser == null) {
            throw new NullUserException("The user does not exist.");
        }

        if (!dbUser.equalsPassword(user.getPassword())) {
            user.encryption(null);

            userRepository.updatePassword(user.getEmail(), user.getPassword());
            redisService.deleteUserToken(user.getEmail());
        }

        throw new SamePasswordException("The two passwords are the same.");
    }
}
