package com.framework.simpleLogin.service;

import com.framework.simpleLogin.entity.User;
import com.framework.simpleLogin.repository.UserRepository;
import com.framework.simpleLogin.utils.Encryption;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Resource
    private UserRepository userRepository;

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

    public User login(User user) {
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

                return dbUser;
            }
        }

        logger.info("User '{}' does not exist or the password is wrong.", user.getEmail());

        return null;
    }
}
