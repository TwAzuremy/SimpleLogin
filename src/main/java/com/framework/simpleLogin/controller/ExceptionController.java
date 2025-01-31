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
        String message = (String) request.getAttribute("filter.exception.message");

        throw new InvalidJwtException(message);
    }

    @RequestMapping("/Exception")
    public void Exception(HttpServletRequest request) throws Exception {
        System.out.println("经过异常控制层");

        throw ((Exception) request.getAttribute("filter.exception"));
    }
}
