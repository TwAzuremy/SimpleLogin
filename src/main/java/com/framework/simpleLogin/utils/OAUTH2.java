package com.framework.simpleLogin.utils;

import com.framework.simpleLogin.annotation.OAuth2ClientValue;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public final class OAUTH2 {
    @OAuth2ClientValue(key = "github")
    public static Map<String, String> GITHUB;

    @OAuth2ClientValue(key = "google")
    public static Map<String, String> GOOGLE;

    public static Map<String, String> get(String key) {
        return switch (key) {
            case "github" -> GITHUB;
            case "google" -> GOOGLE;
            default -> null;
        };
    }
}
