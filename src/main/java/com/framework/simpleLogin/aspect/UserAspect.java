package com.framework.simpleLogin.aspect;

import com.framework.simpleLogin.entity.User;
import com.framework.simpleLogin.exception.NullEmailException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class UserAspect {
    @Around("execution(* com.framework.simpleLogin.service.UserService.login(..)) || " +
            "execution(* com.framework.simpleLogin.service.UserService.register(..))")
    public Object validateUserData(ProceedingJoinPoint pjp) {
        Object[] args = pjp.getArgs();

        if (args != null && args.length > 0 && args[0] instanceof User user) {
            if (user.getEmail() == null || user.getEmail().isEmpty()) {
                throw new NullEmailException("Email cannot be null or empty.");
            }
        }

        try {
            return pjp.proceed();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
