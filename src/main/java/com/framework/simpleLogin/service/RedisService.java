package com.framework.simpleLogin.service;

import com.framework.simpleLogin.utils.CACHENAME;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisService {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private final Logger logger = LoggerFactory.getLogger(RedisService.class);

    public void set(String key, Object value, long expirationTime, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, expirationTime, timeUnit);
    }

    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @CacheEvict(cacheNames = CACHENAME.CAPTCHA, key = "#key")
    public void deleteCaptcha(String key) {
        logger.info("In the cache name '{}', key '{}' is cleared.", CACHENAME.CAPTCHA, key);
    }
}
