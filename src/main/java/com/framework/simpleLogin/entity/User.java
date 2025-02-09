package com.framework.simpleLogin.entity;

import com.framework.simpleLogin.utils.CONSTANT;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "user")
@EqualsAndHashCode(exclude = {"password"})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "username", length = 64, nullable = false)
    private String username;

    @Column(name = "password", length = 96)
    private String password;

    @Column(name = "email", unique = true, nullable = false)
    @Pattern(regexp = CONSTANT.REGEX.EMAIL, message = "Invalid email address")
    private String email;

    @Column(name = "profile")
    private String profile;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OAuthUser> oAuthUsers = new ArrayList<>();
}

