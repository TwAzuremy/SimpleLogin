package com.framework.simpleLogin.factory;

import com.framework.simpleLogin.domain.OAuthUserInfo;
import com.framework.simpleLogin.domain.impl.GithubUserInfo;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

import java.util.Map;

public class OAuthUserInfoFactory {
    public static OAuthUserInfo getUserInfo(String registrationId, Map<String, Object> attributes) {
        return switch (registrationId.toLowerCase()) {
            case "github" -> new GithubUserInfo(attributes);
            default -> throw new OAuth2AuthenticationException("Unsupported OAuth2 provider: " + registrationId);
        };
    }
}
