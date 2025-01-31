package com.framework.simpleLogin.filter;

import com.framework.simpleLogin.event.JwtAuthenticationTokenEvent;
import com.framework.simpleLogin.exception.InvalidJwtException;
import com.framework.simpleLogin.utils.CONSTANT;
import com.framework.simpleLogin.utils.Gadget;
import com.framework.simpleLogin.utils.JwtUtil;
import com.framework.simpleLogin.utils.RedisUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    @Resource
    private RedisUtil redisUtil;

    @Resource
    private ApplicationEventPublisher eventPublisher;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI().substring(request.getContextPath().length()).replaceAll("/+$", "");

        /*
         * Since the ExceptionFilter will also pass through here after forwarding,
         * the exception will be re-triggered if it is not restricted,
         * and then it will be caught by the security default exception handler,
         * returning a 401 http status code.
         */
        if (CONSTANT.OTHER.ALLOWED_PATH.stream().anyMatch(path::contains)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = Gadget.requestTokenProcessing(request.getHeader("Authorization"));

        if (Gadget.StringUtils.isEmpty(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        String email;

        try {
            email = (String) JwtUtil.parse(token).get("email");
            String cacheToken = redisUtil.get(CONSTANT.CACHE_NAME.USER_TOKEN + ":" + email).toString();

            if (!token.equals(cacheToken)) {
                throw new InvalidJwtException("User '" + email + "' Jwt verification failed.");
            }

            eventPublisher.publishEvent(
                    new JwtAuthenticationTokenEvent(this, "User '" + email + "' Jwt Validated.")
            );
        } catch (RuntimeException e) {
            throw new InvalidJwtException("The JWT token is invalid.");
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                email, null, null
        );

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        filterChain.doFilter(request, response);
    }
}
