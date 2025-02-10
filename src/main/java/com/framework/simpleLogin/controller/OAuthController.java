package com.framework.simpleLogin.controller;

import com.framework.simpleLogin.entity.OAuthUser;
import com.framework.simpleLogin.service.OAuthUserService;
import com.framework.simpleLogin.utils.Gadget;
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

    @GetMapping("/redirect/github")
    public ResponseEntity<String> redirect(@RequestParam String code) {
        return new ResponseEntity<>(HttpStatus.OK, code);
    }

    @PostMapping("/login/github")
    public ResponseEntity<String> loginFromGithub(@RequestParam String code) {
        Map<String, String> config = OAUTH2.get("github");

        if (Objects.isNull(config)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST, null);
        }

        // Trade code for token
        String accessToken = oAuthUserService.exchangeCodeForToken(code, config);
        // Use the token to obtain user information
        OAuthUser oAuthUser = oAuthUserService.getUserForToken(accessToken,
                config.get("user-info-uri"), config.get("id"));

        // If the user exists, the user information is obtained, and the token is generated;
        // If the user does not exist, create a new one in the database table and generate a token.
        return new ResponseEntity<>(HttpStatus.OK, oAuthUserService.loadUser(oAuthUser));
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
