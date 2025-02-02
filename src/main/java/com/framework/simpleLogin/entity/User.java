package com.framework.simpleLogin.entity;

import com.framework.simpleLogin.utils.CONSTANT;
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
    @Pattern(regexp = CONSTANT.REGEX.EMAIL, message = "Invalid email address")
    private String email;

    @Column(name = "profile")
    private String profile;
}

