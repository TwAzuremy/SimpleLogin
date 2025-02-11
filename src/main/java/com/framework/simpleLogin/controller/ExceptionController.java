package com.framework.simpleLogin.controller;

import com.framework.simpleLogin.exception.InvalidJwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/exceptions")
public class ExceptionController {
    @RequestMapping("/InvalidJwtException")
    public void InvalidJwtException(HttpServletRequest request) {
        String message = ((Exception) request.getAttribute("filter.exception")).getMessage();
        String email = ((InvalidJwtException) request.getAttribute("filter.exception")).getId();

        throw new InvalidJwtException(message, email);
    }

    @RequestMapping("/Exception")
    public void Exception(HttpServletRequest request) throws Exception {
        throw ((Exception) request.getAttribute("filter.exception"));
    }
}
