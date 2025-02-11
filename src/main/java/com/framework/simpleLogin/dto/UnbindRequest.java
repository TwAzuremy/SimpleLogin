package com.framework.simpleLogin.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UnbindRequest {
    private Long userId;
    private String oauthUserId;
}
