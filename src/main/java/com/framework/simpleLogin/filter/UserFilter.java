package com.framework.simpleLogin.filter;

import com.framework.simpleLogin.exception.InvalidJwtException;
import com.framework.simpleLogin.service.RedisService;
import com.framework.simpleLogin.utils.CACHE_NAME;
import com.framework.simpleLogin.utils.JwtUtils;
import com.framework.simpleLogin.utils.SimpleUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

@Order(1)
@WebFilter(urlPatterns = {"/users/*"})
public class UserFilter implements Filter {
    private static final Set<String> ALLOWED_PATHS = Set.of("/users/register");
    private final JwtUtils jwtUtils = new JwtUtils();

    @Resource
    private RedisService redisService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String path = request.getRequestURI().substring(request.getContextPath().length()).replaceAll("/+$", "");

        if (ALLOWED_PATHS.contains(path)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        String token = request.getHeader("Authorization");

        if (SimpleUtils.stringIsEmpty(token) || !token.startsWith(SimpleUtils.authorizationPrefix)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        try {
            token = token.substring(SimpleUtils.authorizationPrefix.length());

            String email = jwtUtils.getClaims(token).get("email").toString();
            String cacheToken = (String) redisService.get(CACHE_NAME.USER + ":token:" + email);

            if (SimpleUtils.stringIsEmpty(cacheToken) || !jwtUtils.validateToken(token) || !cacheToken.equals(token)) {
                throw new InvalidJwtException("The JWT token is invalid.");
            }
        } catch (RuntimeException e) {
            throw new InvalidJwtException("The JWT token is invalid.");
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
