package com.framework.simpleLogin.factory;

import com.fasterxml.jackson.databind.JsonNode;
import com.framework.simpleLogin.entity.OAuthUser;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Map;

public class OAuthUserProviderFactory {
    public static MultiValueMap<String, String> getSendBody(String code, Map<String, String> config) {
        String provider = config.get("provider").toLowerCase();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", config.get("client-id"));
        params.add("client_secret", config.get("client-secret"));
        params.add("code", code);

        switch (provider) {
            case "github" -> {
            }
            case "google" -> {
                params.add("grant_type", "authorization_code");
                params.add("redirect_uri", config.get("redirect-uri"));
            }
        }

        return params;
    }

    public static OAuthUser getOAuthUser(JsonNode json, String provider) {
        return switch (provider) {
            case "github" -> OAuthUser.builder()
                    .providerId(json.get("node_id").asText())
                    .provider(provider)
                    .username(json.get("login").asText())
                    .email(json.get("email").asText())
                    .profile(json.get("bio").asText())
                    .build();
            case "google" -> OAuthUser.builder()
                    .providerId(json.get("sub").asText())
                    .provider(provider)
                    .username(json.get("name").asText())
                    .email(json.get("email").asText())
                    .build();
            default -> throw new IllegalStateException("Unexpected value: " + provider);
        };
    }
}
