package com.framework.simpleLogin.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailRequest {
    private String email;
    private String username;

    @Override
    public String toString() {
        return email;
    }
}
