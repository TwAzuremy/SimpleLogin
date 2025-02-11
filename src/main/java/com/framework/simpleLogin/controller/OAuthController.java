package com.framework.simpleLogin.controller;

import com.framework.simpleLogin.dto.UnbindRequest;
import com.framework.simpleLogin.entity.OAuthUser;
import com.framework.simpleLogin.service.OAuthUserService;
import com.framework.simpleLogin.utils.Gadget;
import com.framework.simpleLogin.utils.JwtUtil;
import com.framework.simpleLogin.utils.OAUTH2;
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

    @GetMapping("/redirect")
    public ResponseEntity<String> redirect(@RequestParam String code) {
        return new ResponseEntity<>(HttpStatus.OK, code);
    }

    @PostMapping("/login/github")
    public ResponseEntity<String> loginFromGithub(@RequestParam String code) {
        Map<String, String> config = OAUTH2.get("github");

        if (Objects.isNull(config)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST, null);
        }

        OAuthUser oAuthUser = oAuthUserService.codeToToken(code, config);

        // If the user exists, the user information is obtained, and the token is generated;
        // If the user does not exist, create a new one in the database table and generate a token.
        return new ResponseEntity<>(HttpStatus.OK, oAuthUserService.loadUser(oAuthUser));
    }

    @PostMapping("/bind/github")
    public ResponseEntity<Integer> bindGithub(@RequestHeader(value = "Authorization") String userToken, @RequestParam String code) {
        Map<String, String> config = OAUTH2.get("github");
        Map<String, Object> claims = JwtUtil.parse(Gadget.requestTokenProcessing(userToken));
        long userId = Long.parseLong(claims.get("id").toString());

        if (Objects.isNull(config)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST, null);
        }

        OAuthUser oAuthUser = oAuthUserService.codeToToken(code, config);
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
        String template = "?client_id={}&redirect_uri={}&response_type=code&scope={}";
        Map<String, String> oauth2Info = OAUTH2.get(provider.toLowerCase());

        if (!Objects.isNull(oauth2Info)) {
            String request = Gadget.StringUtils.format(template,
                    oauth2Info.get("client-id"),
                    oauth2Info.get("redirect-uri"),
                    oauth2Info.get("scope"));

            return new ResponseEntity<>(HttpStatus.OK, oauth2Info.get("authorization-uri") + request);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST, null);
    }
}
