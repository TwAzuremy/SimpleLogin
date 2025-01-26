package com.framework.simpleLogin.aspect;

import com.framework.simpleLogin.entity.User;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class UserAspect {
    private final Logger logger = LoggerFactory.getLogger(UserAspect.class);

    @Around("execution(* com.framework.simpleLogin.service.UserService.*(..))")
    public Object validateUserData(ProceedingJoinPoint pjp) {
        Object[] args = pjp.getArgs();

        if (args != null && args.length > 0 && args[0] instanceof User user) {
            if (user.getEmail() == null || user.getEmail().isEmpty()) {
                logger.error("User email cannot be null or empty.");
                return null;
            }
        }

        try {
            return pjp.proceed();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }


}
