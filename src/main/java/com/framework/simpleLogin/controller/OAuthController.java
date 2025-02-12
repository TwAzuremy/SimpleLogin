package com.framework.simpleLogin.controller;

import com.framework.simpleLogin.dto.OAuthLoginRequest;
import com.framework.simpleLogin.dto.UnbindRequest;
import com.framework.simpleLogin.entity.OAuthUser;
import com.framework.simpleLogin.service.OAuthUserService;
import com.framework.simpleLogin.utils.*;
import com.framework.simpleLogin.utils.ResponseEntity;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/oauth")
@Slf4j
public class OAuthController {
    @Resource
    private OAuthUserService oAuthUserService;

    @Resource
    private RedisUtil redisUtil;

    @GetMapping("/redirect")
    public ResponseEntity<String> redirect(@RequestParam String code, @RequestParam String state) {
        Object provider = redisUtil.get(CONSTANT.CACHE_NAME.OAUTH2_STATE + ":" + state);

        if (Objects.isNull(provider)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST, "Invalid state", null);
        }

        redisUtil.del(CONSTANT.CACHE_NAME.OAUTH2_STATE + ":" + state);

        return new ResponseEntity<>(HttpStatus.OK, code);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody OAuthLoginRequest request) {
        Map<String, String> config = OAUTH2.get(request.getProvider());

        if (Objects.isNull(config)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST, null);
        }

        OAuthUser oAuthUser = oAuthUserService.codeToToken(request.getCode(), config);

        // If the user exists, the user information is obtained, and the token is generated;
        // If the user does not exist, create a new one in the database table and generate a token.
        return new ResponseEntity<>(HttpStatus.OK, oAuthUserService.loadUser(oAuthUser));
    }

    @PostMapping("/bind")
    public ResponseEntity<Integer> bind(@RequestHeader(value = "Authorization") String userToken,
                                        @RequestBody OAuthLoginRequest request) {
        Map<String, String> config = OAUTH2.get(request.getProvider());
        Map<String, Object> claims = JwtUtil.parse(Gadget.requestTokenProcessing(userToken));
        long userId = Long.parseLong(claims.get("id").toString());

        if (Objects.isNull(config)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST, null);
        }

        OAuthUser oAuthUser = oAuthUserService.codeToToken(request.getCode(), config);
        int result = oAuthUserService.bindUser(oAuthUser, userId);

        if (result == -1) {
            return new ResponseEntity<>(HttpStatus.CONFLICT, "The account has been linked to another user.", null);
        } else if (result == 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST, "Account binding failed.", null);
        }

        return new ResponseEntity<>(HttpStatus.OK, result);
    }

    @DeleteMapping("/unbind")
    public ResponseEntity<Integer> unbind(@RequestBody UnbindRequest request) {
        return new ResponseEntity<>(HttpStatus.OK,
                oAuthUserService.unbind(request.getUserId(), request.getOauthUserId()));
    }

    @GetMapping("/get-redirect-address")
    public ResponseEntity<String> getRedirectAddress(@RequestParam("provider") String provider) {
        String template = "?client_id={}&redirect_uri={}&response_type=code&scope={}&state={}";

        Map<String, String> config = OAUTH2.get(provider.toLowerCase());
        String state = Encryption.generateState();

        if (!Objects.isNull(config)) {
            redisUtil.set(CONSTANT.CACHE_NAME.OAUTH2_STATE + ":" + state,
                    provider, CONSTANT.CACHE_EXPIRATION_TIME.OAUTH2_STATE);
            String request = Gadget.StringUtils.format(template,
                    config.get("client-id"),
                    config.get("redirect-uri"),
                    config.get("scope"),
                    state);

            return new ResponseEntity<>(HttpStatus.OK, config.get("authorization-uri") + request);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST, null);
    }
}
