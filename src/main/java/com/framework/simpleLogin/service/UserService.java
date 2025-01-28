package com.framework.simpleLogin.service;

import com.framework.simpleLogin.dto.UserDTO;
import com.framework.simpleLogin.entity.User;
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

        String salt = Encryption.generateSalt();
        String ciphertext = Encryption.SHA256(user.getPassword() + salt);

        user.setPassword(ciphertext + salt);
        userRepository.save(user);

        redisService.delete(CACHE_NAME.USER + ":cache:" + user.getEmail());

        return true;
    }

    public Map<String, Object> login(User user) {
        User dbUser = userRepository.findByEmail(user.getEmail());

        if (dbUser != null) {
            // Take out the stored ciphertext and salt.
            String dbCiphertext = dbUser.getPassword().substring(0, 64);
            String dbSalt = dbUser.getPassword().substring(64, 96);

            // Re-encrypt the password and salt together.
            String ciphertext = Encryption.SHA256(user.getPassword() + dbSalt);

            // Compare the two ciphertexts.
            if (ciphertext.equals(dbCiphertext)) {
                return new HashMap<>() {
                    {
                        put("user", new UserDTO(dbUser));
                        put("token", jwtUtils.generateTokenForUser(new UserDTO(dbUser)));
                    }
                };
            }
        }

        loginAttemptService.failed(user.getEmail());

        return null;
    }

    public Map<String, Object> verifyByToken(String token) {
        return jwtUtils.getClaims(token.substring(SimpleUtils.authorizationPrefix.length()));
    }
}
