package com.framework.simpleLogin.service;

import com.framework.simpleLogin.domain.AuthenticationDetails;
import com.framework.simpleLogin.dto.UserResponse;
import com.framework.simpleLogin.entity.User;
import com.framework.simpleLogin.exception.InvalidAccountOrPasswordException;
import com.framework.simpleLogin.utils.JwtUtil;
import jakarta.annotation.Resource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class AuthenticationService {
    @Resource
    private AuthenticationManager authenticationManager;

    public String login(User user) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                user.getEmail(), user.getPassword()
        );

        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        if (Objects.isNull(authentication)) {
            throw new InvalidAccountOrPasswordException("The account or password is incorrect", user.getEmail());
        }

        UserResponse response = ((AuthenticationDetails) authentication.getPrincipal()).getUser();

        return JwtUtil.generate(response.toMap());
    }

    public String logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return (String) authentication.getPrincipal();
    }
}
