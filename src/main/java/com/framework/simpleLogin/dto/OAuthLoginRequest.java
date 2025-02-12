package com.framework.simpleLogin.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OAuthLoginRequest {
    private String code;
    private String provider;

    public String getProvider() {
        return provider.toLowerCase();
    }
}
