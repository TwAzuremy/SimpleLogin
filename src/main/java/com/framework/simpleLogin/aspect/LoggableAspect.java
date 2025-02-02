package com.framework.simpleLogin.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggableAspect {
    private static final Logger logger = LoggerFactory.getLogger(LoggableAspect.class);

    @Around("@annotation(com.framework.simpleLogin.annotation.Loggable)")
    public Object logMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        if (logger.isInfoEnabled()) {
            logger.info("Entering method: [{}] with arguments: {}", methodName, Arrays.toString(args));
        }

        long startTime = System.currentTimeMillis();
        Object result;

        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            logger.error("Exception in method [{}]: {}", methodName, e.getMessage(), e);
            throw e;
        } finally {
            long executionTime = System.currentTimeMillis() - startTime;

            logger.info("Exiting method: [{}] with arguments: {} ms", methodName, executionTime);
        }

        return result;
    }
}
