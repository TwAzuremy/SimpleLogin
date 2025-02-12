package com.framework.simpleLogin.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "user")
@EqualsAndHashCode(exclude = {"password"})
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", length = 64, nullable = false)
    private String username;

    @Column(name = "password", length = 96)
    private String password;

    @Column(name = "name", length = 64)
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "profile")
    private String profile;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private final List<OAuthUser> oAuthUsers = new ArrayList<>();
}

