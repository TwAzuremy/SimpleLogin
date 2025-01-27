package com.framework.simpleLogin.dto;

import com.framework.simpleLogin.entity.User;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class UserDTO {
    private int id;
    private String username;
    private String email;

    public UserDTO(@NonNull User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
    }

    public UserDTO(Map<String, Object> claims) {
        this.id = (int) claims.get("id");
        this.username = (String) claims.get("username");
        this.email = (String) claims.get("email");
    }
}
