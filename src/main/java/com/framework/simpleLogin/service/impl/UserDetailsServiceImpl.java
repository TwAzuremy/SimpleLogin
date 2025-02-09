package com.framework.simpleLogin.service.impl;

import com.framework.simpleLogin.domain.AuthenticationDetails;
import com.framework.simpleLogin.entity.User;
import com.framework.simpleLogin.repository.UserRepository;
import jakarta.annotation.Resource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Resource
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByEmail(username).orElse(null);

        if (Objects.isNull(user)) {
            throw new UsernameNotFoundException("User not found");
        }

        return new AuthenticationDetails(user);
    }
}
