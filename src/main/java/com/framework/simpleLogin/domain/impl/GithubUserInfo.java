package com.framework.simpleLogin.domain.impl;

import com.framework.simpleLogin.domain.OAuthUserInfo;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class GithubUserInfo implements OAuthUserInfo {
    private Map<String, Object> attributes;

    @Override
    public String getProviderId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getUsername() {
        return (String) attributes.get("login");
    }

    @Override
    public String getEmail() {
        return (String) ((List<?>) attributes.get("email")).get(0);
    }
}
