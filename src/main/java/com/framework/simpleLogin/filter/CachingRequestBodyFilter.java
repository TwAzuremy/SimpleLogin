package com.framework.simpleLogin.filter;

import com.framework.simpleLogin.utils.Gadget;
import com.framework.simpleLogin.wrapper.ContentCachingRequestWrapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class CachingRequestBodyFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Checks if the request content type is JSON and wraps the request to cache its content if necessary.
        if (!Gadget.StringUtils.isEmpty(request.getContentType()) && request.getContentType().contains("application/json")) {
            // Wrap the request to cache its content if it's a JSON request
            filterChain.doFilter(new ContentCachingRequestWrapper(request), response);
        } else {
            // Continue processing the request without caching its content
            filterChain.doFilter(request, response);
        }
    }
}
