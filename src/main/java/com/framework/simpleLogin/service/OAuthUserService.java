package com.framework.simpleLogin.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.framework.simpleLogin.dto.OAuthUserResponse;
import com.framework.simpleLogin.dto.UserResponse;
import com.framework.simpleLogin.entity.OAuthUser;
import com.framework.simpleLogin.entity.User;
import com.framework.simpleLogin.exception.ExpiredCodeOrTokenException;
import com.framework.simpleLogin.exception.MissingUserException;
import com.framework.simpleLogin.factory.OAuthUserProviderFactory;
import com.framework.simpleLogin.repository.OAuthUserRepository;
import com.framework.simpleLogin.repository.UserRepository;
import com.framework.simpleLogin.utils.CONSTANT;
import com.framework.simpleLogin.utils.JwtUtil;
import com.framework.simpleLogin.utils.RedisUtil;
import jakarta.annotation.Resource;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Service
public class OAuthUserService {
    @Resource
    private RedisUtil redisUtil;

    @Resource
    private OAuthUserRepository oAuthUserRepository;

    @Resource
    private UserRepository userRepository;

    public String exchangeCodeForToken(String code, Map<String, String> config) {
        MultiValueMap<String, String> params = OAuthUserProviderFactory.getSendBody(code, config);

        HttpHeaders headers = new HttpHeaders() {
            {
                setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            }
        };

        // Send the code to the server in exchange for a token
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);
        JsonNode response = new RestTemplate().postForObject(config.get("token-uri"), httpEntity, JsonNode.class);

        try {
            if (response != null && !response.isMissingNode() && !response.isNull()) {
                return response.get("access_token").asText();
            }
        } catch (NullPointerException e) {
            throw new ExpiredCodeOrTokenException("The code has expired: " + code);
        }

        throw new ExpiredCodeOrTokenException("The code has expired: " + code);
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

        if (userInfo != null && !userInfo.isMissingNode() && !userInfo.isNull()) {
            return OAuthUserProviderFactory.getOAuthUser(userInfo, provider);
        }

        throw new ExpiredCodeOrTokenException("The token has expired: " + accessToken);
    }

    public OAuthUser codeToToken(String code, Map<String, String> config) {
        // Trade code for token
        String accessToken = this.exchangeCodeForToken(code, config);
        // Use the token to obtain user information
        return this.getUserForToken(accessToken, config.get("user-info-uri"), config.get("provider"));
    }

    @Transactional
    public String loadUser(OAuthUser oAuthUser) {
        Optional<OAuthUser> dbOAuthUser = oAuthUserRepository.findByProviderAndProviderId(
                oAuthUser.getProvider(), oAuthUser.getProviderId());

        if (dbOAuthUser.isPresent()) {
            User user = dbOAuthUser.get().getUser();

            // If there is a bound user, the user's token is used.
            if (user != null) {
                UserResponse response = new UserResponse(user);
                String token = JwtUtil.generate(response.toMap());

                redisUtil.setUserToken(response.generateCacheName(), token);

                return token;
            }

            // If no user is bound, generate your own token.
            return cacheToken(dbOAuthUser.get());
        }

        // Set as a unique username
        oAuthUser.setUsername(oAuthUser.getUsername() + "#" + oAuthUser.getProvider() + "-" + oAuthUser.getProviderId());
        oAuthUserRepository.save(oAuthUser);

        return cacheToken(oAuthUser);
    }

    @Transactional
    public int bindUser(OAuthUser oAuthUser, long userId) {
        Optional<OAuthUser> dbOAuthUser = oAuthUserRepository.findByProviderAndProviderId(
                oAuthUser.getProvider(), oAuthUser.getProviderId());

        Optional<UserResponse> dbUser = userRepository.findUserExcludePasswordById(userId);

        if (dbUser.isEmpty()) {
            throw new MissingUserException("User not found");
        }

        // If no third-party user exists, create a new one
        if (dbOAuthUser.isEmpty()) {
            oAuthUser.setUser(dbUser.get().toUser());
            oAuthUser.setUsername(oAuthUser.getUsername() + "#" + oAuthUser.getProvider() + "-" + oAuthUser.getProviderId());
            oAuthUserRepository.save(oAuthUser);

            return 1;
        }

        // TODO 可能需要验证用户是否已经绑定同一个 Provider 的第三方账号
        // Determine whether the account has been bound by another user.
        if (dbOAuthUser.get().getUser() != null) {
            return -1;
        }

        // If it exists, modify the user_id
        int result = oAuthUserRepository.updateUserIdById(dbUser.get().getId(), dbOAuthUser.get().getId());

        if (result > 0) {
            redisUtil.del(CONSTANT.CACHE_NAME.USER_CACHE + ":oauth2:" + dbOAuthUser.get().getId());
            redisUtil.del(CONSTANT.CACHE_NAME.USER_CACHE + ":info:" + dbUser.get().getId());
        }

        return result;
    }

    @Transactional
    public int unbind(long userId, String oauthUserId) {
        int result = oAuthUserRepository.deleteOAuthUserByUserIdAndId(userId, oauthUserId);

        if (result > 0) {
            redisUtil.del(CONSTANT.CACHE_NAME.USER_CACHE + ":oauth2:" + oauthUserId);
            redisUtil.del(CONSTANT.CACHE_NAME.USER_CACHE + ":info:" + userId);
        }

        return result;
    }

    private String cacheToken(OAuthUser oAuthUser) {
        OAuthUserResponse response = new OAuthUserResponse(oAuthUser);
        String token = JwtUtil.generate(response.toMap());

        redisUtil.set(CONSTANT.CACHE_NAME.USER_TOKEN + ":" + response.generateCacheName()
                , token, CONSTANT.CACHE_EXPIRATION_TIME.USER_TOKEN);

        return token;
    }

    @Cacheable(cacheNames = CONSTANT.CACHE_NAME.USER_CACHE + ":oauth2@user-cache", key = "#id")
    public Object getInfo(String id) {
        OAuthUser oAuthUser = oAuthUserRepository.findById(id).orElseThrow(() -> new MissingUserException("User not found"));

        if (oAuthUser.getUser() == null) {
            return oAuthUser;
        }

        return new UserResponse(oAuthUser.getUser());
    }
}
