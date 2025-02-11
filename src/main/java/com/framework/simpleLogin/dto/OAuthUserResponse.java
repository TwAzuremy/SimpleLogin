package com.framework.simpleLogin.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.framework.simpleLogin.entity.OAuthUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OAuthUserResponse extends UserResponse {
    @JsonIgnore
    private String TABLE = "oauth2_user";

    private String uuid;
    private String provider;
    private String providerId;

    public OAuthUserResponse(OAuthUser oAuthUser) {
        super(oAuthUser);

        this.uuid = oAuthUser.getId();
        this.provider = oAuthUser.getProvider();
        this.providerId = oAuthUser.getProviderId();
    }

    @Override
    public List<OAuthUserResponse> getOAuthUserResponses() {
        return null;
    }

    @Override
    public String sign() {
        return DigestUtils.md5DigestAsHex(
                (TABLE + uuid + super.getUsername() + super.getEmail() + provider + providerId)
                        .getBytes(StandardCharsets.UTF_8)
        );
    }

    @Override
    public Map<String, Object> toMap() {
        return Map.of(
                "table", TABLE,
                "id", uuid,
                "username", super.getUsername(),
                "email", super.getEmail(),
                "provider", this.provider,
                "providerId", this.providerId,
                "sign", this.sign()
        );
    }
}
