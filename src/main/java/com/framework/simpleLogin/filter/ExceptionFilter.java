package com.framework.simpleLogin.filter;

import com.framework.simpleLogin.exception.InvalidJwtException;
import jakarta.servlet.*;

import java.io.IOException;

public class ExceptionFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (InvalidJwtException e) {
            servletRequest.setAttribute("filter.exception", e);
            servletRequest.getRequestDispatcher("/exceptions/InvalidJwtException").forward(servletRequest, servletResponse);
        } catch (Exception e) {
            servletRequest.setAttribute("filter.exception", e);
            servletRequest.getRequestDispatcher("/exceptions/Exception").forward(servletRequest, servletResponse);
        }
    }
}
