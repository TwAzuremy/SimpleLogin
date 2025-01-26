package com.framework.simpleLogin.service;

import com.framework.simpleLogin.dto.UserDTO;
import com.framework.simpleLogin.entity.User;
import com.framework.simpleLogin.repository.UserRepository;
import com.framework.simpleLogin.utils.Encryption;
import com.framework.simpleLogin.utils.JwtUtils;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {
    @Resource
    private UserRepository userRepository;

    @Resource
    private JwtUtils jwtUtils;

    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    public User register(User user) {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            logger.info("User with email '{}' already exists.", user.getEmail());

            return null;
        }

        String salt = Encryption.generateSalt();
        String ciphertext = Encryption.SHA256(user.getPassword() + salt);

        // TODO: This line of code is for testing purposes only.
        //       The production environment needs to be removed.
        //       Avoid leaking user information.
        logger.info("User '{}' Plaintext Password: {}, Salt: {}, Ciphertext Password: {}.", user.getEmail(), user.getPassword(), salt, ciphertext);

        user.setPassword(ciphertext + salt);
        User dbUser = userRepository.save(user);

        if (userRepository.count() > 0) {
            logger.info("User '{}' registered successfully.", user.getEmail());

            return dbUser;
        }

        return null;
    }

    public Map<String, Object> login(User user) {
        Map<String, Object> result = new HashMap<>();

        User dbUser = userRepository.findByEmail(user.getEmail());

        if (dbUser != null) {
            // Take out the stored ciphertext and salt.
            String dbCiphertext = dbUser.getPassword().substring(0, 64);
            String dbSalt = dbUser.getPassword().substring(64, 96);

            // Re-encrypt the password and salt together.
            String ciphertext = Encryption.SHA256(user.getPassword() + dbSalt);

            // TODO: This line of code is for testing purposes only.
            //       The production environment needs to be removed.
            //       Avoid leaking user information.
            logger.info("User '{}' login password: {}, ciphertext: {}.", user.getEmail(), user.getPassword(), ciphertext);

            // Compare the two ciphertexts.
            if (ciphertext.equals(dbCiphertext)) {
                logger.info("User '{}' logged in successfully.", user.getEmail());

                result.put("user", new UserDTO(dbUser));
                result.put("token", jwtUtils.generateTokenForUser(new UserDTO(dbUser)));

                return result;
            }
        }

        logger.info("User '{}' does not exist or the password is wrong.", user.getEmail());

        return null;
    }

    public User getUserFromToken(String token) {
        Map<String, Object> claims = jwtUtils.getClaims(token);

        if (claims != null && !claims.isEmpty()) {
            User user = new User();
            user.setId((Integer) claims.get("id"));
            user.setUsername((String) claims.get("username"));
            user.setEmail((String) claims.get("email"));

            if (userRepository.findByEmail(user.getEmail()) != null) {
                return user;
            }
        }

        return null;
    }
}
