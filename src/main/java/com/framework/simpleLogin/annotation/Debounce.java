package com.framework.simpleLogin.annotation;

import com.framework.simpleLogin.utils.CONSTANT;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Debounce {
    String key();
    long timeout() default CONSTANT.CACHE_EXPIRATION_TIME.API_DEBOUNCE_TIME;
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;
}
