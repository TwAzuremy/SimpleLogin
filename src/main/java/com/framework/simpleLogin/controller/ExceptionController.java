package com.framework.simpleLogin.controller;

import com.framework.simpleLogin.exception.InvalidJwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/exception")
public class ExceptionController {
    @RequestMapping("/InvalidJwtException")
    public void InvalidJwtException() {
        throw new InvalidJwtException("The JWT token is invalid.");
    }

    @RequestMapping("/Exception")
    public void Exception(HttpServletRequest request) throws Exception {
        throw ((Exception) request.getAttribute("filter.exception"));
    }
}
