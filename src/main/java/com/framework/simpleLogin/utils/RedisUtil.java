package com.framework.simpleLogin.utils;

import jakarta.annotation.Resource;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    public void set(String key, Object value, long expirationTime, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, expirationTime, timeUnit);
    }

    public void increment(String key, int step) {
        redisTemplate.opsForValue().increment(key, step);
    }

    public void del(String key) {
        redisTemplate.delete(key);
    }

    @CacheEvict(value = CONSTANT.CACHE_NAME.CAPTCHA_REGISTER, key = "#email")
    public void delCaptchaRegister(String email) {}

    @CacheEvict(value = CONSTANT.CACHE_NAME.USER_TOKEN, key = "#email")
    public void delUserToken(String email) {}

    @CacheEvict(value = CONSTANT.CACHE_NAME.USER_CACHE, key = "#email")
    public void delUserCache(String email) {}
}
