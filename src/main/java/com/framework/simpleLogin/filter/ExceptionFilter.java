package com.framework.simpleLogin.filter;

import com.framework.simpleLogin.exception.InvalidJwtException;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Order(-1)
@WebFilter(urlPatterns = "/*")
public class ExceptionFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (InvalidJwtException e) {
            servletRequest.getRequestDispatcher("/exception/InvalidJwtException").forward(servletRequest, servletResponse);
        } catch (Exception e) {
            servletRequest.setAttribute("filter.exception", e);
            servletRequest.getRequestDispatcher("/exception/Exception").forward(servletRequest, servletResponse);
        }
    }
}
