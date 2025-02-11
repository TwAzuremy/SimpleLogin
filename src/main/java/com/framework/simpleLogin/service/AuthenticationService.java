package com.framework.simpleLogin.service;

import com.framework.simpleLogin.domain.AuthenticationDetails;
import com.framework.simpleLogin.dto.UserLoginRequest;
import com.framework.simpleLogin.dto.UserResponse;
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

    public UserResponse login(UserLoginRequest user) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                user.getUsername(), user.getPassword()
        );

        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        if (Objects.isNull(authentication)) {
            throw new InvalidAccountOrPasswordException("The account or password is incorrect", user.getUsername(), 0);
        }

        return ((AuthenticationDetails) authentication.getPrincipal()).getUser();
    }

    public String logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return (String) authentication.getPrincipal();
    }
}
