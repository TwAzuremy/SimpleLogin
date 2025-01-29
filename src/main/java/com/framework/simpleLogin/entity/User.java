package com.framework.simpleLogin.entity;

import com.framework.simpleLogin.utils.Encryption;
import com.framework.simpleLogin.utils.SimpleUtils;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "user")
@EqualsAndHashCode(exclude = {"password"})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "username", length = 64, nullable = false)
    private String username;

    @Column(name = "password", length = 96, nullable = false)
    private String password;

    @Column(name = "email", nullable = false)
    @Pattern(regexp = "^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = "Invalid email address")
    private String email;

    public boolean isEmpty() {
        return SimpleUtils.stringIsEmpty(email) || SimpleUtils.stringIsEmpty(password);
    }

    public User encryption(String salt) {
        if (SimpleUtils.stringIsEmpty(salt)) {
            salt = Encryption.generateSalt();
        }

        String ciphertext = Encryption.SHA256(password + salt);

        password = ciphertext + salt;

        return this;
    }

    public boolean equalsPassword(String determinedPassword) {
        // Take out the stored ciphertext and salt.
        String dbCiphertext = password.substring(0, 64);
        String dbSalt = password.substring(64, 96);

        // Re-encrypt the password and salt together.
        String ciphertext = Encryption.SHA256(determinedPassword + dbSalt);

        return dbCiphertext.equals(ciphertext);
    }
}
