package com.framework.simpleLogin.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public final class OAUTH2 {
    public static Map<String, String> GITHUB;

    public OAUTH2(
            @Value("${spring.security.oauth2.client.registration.github.client-id}") String GITHUB_CLIENT_ID,
            @Value("${spring.security.oauth2.client.registration.github.client-secret}") String GITHUB_CLIENT_SECRET,
            @Value("${spring.security.oauth2.client.provider.github.authorization-uri}") String GITHUB_AUTHORIZATION_URI,
            @Value("${spring.security.oauth2.client.registration.github.redirect-uri}") String GITHUB_REDIRECT_URI
    ) {
        GITHUB = Map.of("client-id", GITHUB_CLIENT_ID, "client-secret", GITHUB_CLIENT_SECRET,
                "authorization-uri", GITHUB_AUTHORIZATION_URI, "redirect-uri", GITHUB_REDIRECT_URI);
    }

    public static Map<String, String> get(String key) {
        return switch (key) {
            case "github" -> GITHUB;
            default -> null;
        };
    }
}
