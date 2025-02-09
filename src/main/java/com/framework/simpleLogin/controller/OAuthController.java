package com.framework.simpleLogin.controller;

import com.framework.simpleLogin.utils.Gadget;
import com.framework.simpleLogin.utils.OAUTH2;
import com.framework.simpleLogin.utils.ResponseEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/oauth")
@Slf4j
public class OAuthController {
    @GetMapping("/redirect")
    public void redirect(@RequestParam("code") String code) {
        // TODO 验证 code
        log.info("code: {}", code);
    }

    @GetMapping("/get-redirect-address")
    public ResponseEntity<String> getRedirectAddress(@RequestParam("provider") String provider) {
        String template = "?client_id={}&redirect_uri={}&response_type=code&scope={}";
        Map<String, String> oauth2Info = OAUTH2.get(provider.toLowerCase());

        if (!Objects.isNull(oauth2Info)) {
            String url = Gadget.StringUtils.format(template, oauth2Info.get("client-id"), oauth2Info.get("redirect-uri"), "user:email");

            return new ResponseEntity<>(HttpStatus.OK, oauth2Info.get("authorization-uri") + url);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST, null);
    }
}
