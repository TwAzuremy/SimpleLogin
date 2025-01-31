package com.framework.simpleLogin.aspect;

import com.framework.simpleLogin.annotation.Debounce;
import com.framework.simpleLogin.utils.CONSTANT;
import com.framework.simpleLogin.utils.RedisUtil;
import jakarta.annotation.Resource;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class DebounceAspect {
    private final ExpressionParser parser = new SpelExpressionParser();

    @Resource
    private RedisUtil redisUtil;

    @Around("@annotation(debounce)")
    public Object debounce(ProceedingJoinPoint joinPoint, Debounce debounce) throws Throwable {
        String paramKey = generateKey(joinPoint, debounce.key());
        String redisKey = CONSTANT.CACHE_NAME.API_DEBOUNCE + ":" + paramKey;

        if (Boolean.TRUE.equals(redisUtil.hasKey(redisKey))) {
            throw new RuntimeException("The request is too frequent, please try again later.");
        }

        redisUtil.set(redisKey, "locked", debounce.timeout(), debounce.timeUnit());

        return joinPoint.proceed();
    }

    private String generateKey(ProceedingJoinPoint joinPoint, String keyExpression) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Object[] args = joinPoint.getArgs();

        StandardEvaluationContext context = new StandardEvaluationContext();
        String[] params = signature.getParameterNames();

        for (int i = 0; i < params.length; i++) {
            context.setVariable(params[i], args[i]);
        }

        return parser.parseExpression(keyExpression).getValue(context, String.class);
    }
}
