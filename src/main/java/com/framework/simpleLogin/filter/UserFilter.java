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
import org.springframework.core.annotation.Order;

import java.io.IOException;

@Order(1)
@WebFilter(urlPatterns = {"/user/login", "/user/verify"})
public class UserFilter implements Filter {
    private final JwtUtils jwtUtils = new JwtUtils();

    @Resource
    private RedisService redisService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String token = ((HttpServletRequest) servletRequest).getHeader("Authorization");

        if (SimpleUtils.stringIsEmpty(token) || !token.startsWith(SimpleUtils.authorizationPrefix)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        try {
            token = token.substring(SimpleUtils.authorizationPrefix.length());

            String email = jwtUtils.getClaims(token).get("email").toString();
            String isExists = (String) redisService.get(CACHE_NAME.USER + ":token:" + email);

            if (SimpleUtils.stringIsEmpty(isExists) || !jwtUtils.validateToken(token) || !isExists.equals(token)) {
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
