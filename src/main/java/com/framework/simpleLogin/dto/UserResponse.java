package com.framework.simpleLogin.dto;

import com.framework.simpleLogin.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class UserResponse {
    private int id;
    private String username;
    private String email;
    private String profile;

    public UserResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.profile = user.getProfile();
    }

    public UserResponse(Map<String, Object> map) {
        this.id = (int) map.get("id");
        this.username = (String) map.get("username");
        this.email = (String) map.get("email");
        this.profile = (String) map.get("profile");
    }

    public Map<String, Object> toMap() {
        return Map.of(
                "id", id,
                "username", username,
                "email", email,
                "profile", profile
        );
    }
}
