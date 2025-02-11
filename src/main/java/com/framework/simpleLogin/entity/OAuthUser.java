package com.framework.simpleLogin.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;


@Getter
@Setter
@Entity
@Table(name = "oauth2_user")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OAuthUser {
    @Id
    @GeneratedValue(generator = "UUID")
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "provider", nullable = false)
    private String provider;

    @Column(name = "provider_id", nullable = false)
    private String providerId;

    @Column(name = "username", length = 64)
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "profile")
    private String profile;
}
