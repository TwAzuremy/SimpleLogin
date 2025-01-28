package com.framework.simpleLogin.service;

import com.framework.simpleLogin.utils.CACHE_NAME;
import jakarta.annotation.Resource;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisService {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    public void set(String key, Object value, long expirationTime, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, expirationTime, timeUnit);
    }

    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void increment(String key, int step) {
        redisTemplate.opsForValue().increment(key, step);
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }

    @CacheEvict(cacheNames = CACHE_NAME.CAPTCHA, key = "#key")
    public void deleteCaptcha(String key) {
    }

    @CacheEvict(cacheNames = CACHE_NAME.USER + ":token", key = "#key")
    public void deleteUserToken(String key) {
    }
}
