package com.framework.simpleLogin.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.framework.simpleLogin.dto.UserResponse;
import com.framework.simpleLogin.entity.OAuthUser;
import com.framework.simpleLogin.entity.User;
import com.framework.simpleLogin.exception.UnableConnectServerException;
import com.framework.simpleLogin.utils.CONSTANT;
import com.framework.simpleLogin.utils.JwtUtil;
import com.framework.simpleLogin.utils.RedisUtil;
import jakarta.annotation.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;

@Service
public class OAuthUserService {
    @Resource
    private UserService userService;

    @Resource
    private RedisUtil redisUtil;

    public String exchangeCodeForToken(String code, Map<String, String> config) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", config.get("client-id"));
        params.add("client_secret", config.get("client-secret"));
        params.add("code", code);

        HttpHeaders headers = new HttpHeaders() {
            {
                setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            }
        };

        // Send the code to the server in exchange for a token
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);
        JsonNode response = new RestTemplate().postForObject(config.get("token-uri"), httpEntity, JsonNode.class);

        if (response != null && !response.isEmpty()) {
            return response.get("access_token").asText();
        }

        throw new UnableConnectServerException("Unable to connect to: " + config.get("token-uri"));
    }

    public OAuthUser getUserForToken(String accessToken, String uri, String provider) {
        HttpHeaders headers = new HttpHeaders() {
            {
                set("Authorization", CONSTANT.OTHER.AUTHORIZATION_PREFIX + accessToken);
            }
        };

        // Send the token to the server to retrieve the user information
        HttpEntity<String> entity = new HttpEntity<>(headers);
        JsonNode userInfo = new RestTemplate().exchange(
                uri, HttpMethod.GET, entity, JsonNode.class
        ).getBody();

        if (userInfo != null && !userInfo.isEmpty()) {
            User user = new User();
            user.setUsername(userInfo.get("login").asText());
            user.setEmail(userInfo.get("email").asText());
            user.setProfile(userInfo.get("bio").asText());

            OAuthUser oAuthUser = new OAuthUser();
            oAuthUser.setUser(user);
            oAuthUser.setProvider(provider);
            oAuthUser.setProviderId(userInfo.get("node_id").asText());

            return oAuthUser;
        }

        throw new UnableConnectServerException("Unable to connect to: " + uri);
    }

    public String loadUser(OAuthUser oAuthUser) {
        User user = userService.findOrCreateUser(oAuthUser);
        String token = JwtUtil.generate(new UserResponse(user).toMap());

        redisUtil.set(
                CONSTANT.CACHE_NAME.USER_TOKEN + ":" + user.getUsername(),
                token,
                CONSTANT.CACHE_EXPIRATION_TIME.USER_TOKEN
        );

        return token;
    }
}
