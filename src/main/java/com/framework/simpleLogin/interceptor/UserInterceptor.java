package com.framework.simpleLogin.interceptor;

import com.framework.simpleLogin.exception.InvalidJwtException;
import com.framework.simpleLogin.listener.JpaValidationEvent;
import com.framework.simpleLogin.service.RedisService;
import com.framework.simpleLogin.utils.CACHE_NAME;
import com.framework.simpleLogin.utils.JwtUtils;
import com.framework.simpleLogin.utils.SimpleUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Set;

@Component
public class UserInterceptor implements HandlerInterceptor {
    private static final Set<String> ALLOWED_PATHS = Set.of("/users/register");
    private final JwtUtils jwtUtils = new JwtUtils();

    @Resource
    private ApplicationEventPublisher eventPublisher;

    @Resource
    private RedisService redisService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String path = request.getRequestURI().substring(request.getContextPath().length()).replaceAll("/+$", "");

        if (ALLOWED_PATHS.contains(path)) {
            return true;
        }

        String token = request.getHeader("Authorization");

        if (SimpleUtils.stringIsEmpty(token) || !token.startsWith(SimpleUtils.authorizationPrefix)) {
            return true;
        }

        try {
            token = token.substring(SimpleUtils.authorizationPrefix.length());

            String email = jwtUtils.getClaims(token).get("email").toString();
            String cacheToken = (String) redisService.get(CACHE_NAME.USER + ":token:" + email);

            if (SimpleUtils.stringIsEmpty(cacheToken) || !jwtUtils.validateToken(token) || !cacheToken.equals(token)) {
                eventPublisher.publishEvent(
                        new JpaValidationEvent(this, "User '" + email + "', JPA validation failed.", false)
                );

                throw new InvalidJwtException("The JWT token is invalid.");
            }

            eventPublisher.publishEvent(
                    new JpaValidationEvent(this, "User '" + email + "', JPA validation successful.", true)
            );
        } catch (RuntimeException e) {
            throw new InvalidJwtException("The JWT token is invalid.");
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
    }
}
