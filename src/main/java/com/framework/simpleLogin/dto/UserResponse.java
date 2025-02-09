package com.framework.simpleLogin.dto;

import com.framework.simpleLogin.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private long id;
    private String username;
    private String email;
    private String profile;

    public UserResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.profile = user.getProfile();
    }

    public Map<String, Object> toMap() {
        return Map.of(
                "id", id,
                "email", email
        );
    }
}
