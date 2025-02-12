package com.framework.simpleLogin.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.framework.simpleLogin.entity.OAuthUser;
import com.framework.simpleLogin.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {
    @JsonIgnore
    private String TABLE = "user";

    private Long id;
    private String username;
    private String email;
    private String profile;

    private List<OAuthUserResponse> oAuthUserResponses = new ArrayList<>();

    public UserResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.profile = user.getProfile();

        user.getOAuthUsers().forEach(oAuthUser -> this.oAuthUserResponses.add(new OAuthUserResponse(oAuthUser)));
    }

    public UserResponse(OAuthUser oAuthUser) {
        this.username = oAuthUser.getUsername();
        this.email = oAuthUser.getEmail();
        this.profile = oAuthUser.getProfile();
    }

    public User toUser() {
        return User.builder()
                .id(id)
                .username(username)
                .email(email)
                .profile(profile)
                .build();
    }

    public String generateCacheName() {
        return TABLE + "*" + id + "-" + this.sign();
    }

    public String sign() {
        return DigestUtils.md5DigestAsHex((TABLE + id + username + email).getBytes(StandardCharsets.UTF_8));
    }

    public Map<String, Object> toMap() {
        return Map.of(
                "table", TABLE,
                "id", id,
                "username", username,
                "email", email,
                "sign", sign()
        );
    }
}
