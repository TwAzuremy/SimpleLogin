package com.framework.simpleLogin.utils;

import jakarta.annotation.Resource;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Utility class for interacting with Redis.
 * <p>
 * This class provides a simple interface for performing common Redis operations,
 * such as getting, setting, and deleting values.
 *
 */
@Component
public class RedisUtil {

    /**
     * The Redis template used to interact with Redis.
     */
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * Retrieves a value from Redis by its key.
     *
     * @param key the key of the value to retrieve
     * @return the value associated with the key, or null if no value is found
     */
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * Checks if a key exists in Redis.
     *
     * @param key the key to check
     * @return true if the key exists, false otherwise
     */
    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * Sets a value in Redis with a specified expiration time.
     *
     * @param key the key of the value to set
     * @param value the value to set
     * @param expirationTime the expiration time in the specified time unit
     * @param timeUnit the time unit of the expiration time
     */
    public void set(String key, Object value, long expirationTime, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, expirationTime, timeUnit);
    }

    /**
     * Sets a value in Redis with a specified expiration time, defaulting to milliseconds.
     *
     * @param key the key of the value to set
     * @param value the value to set
     * @param expirationTime the expiration time in milliseconds
     */
    public void set(String key, Object value, long expirationTime) {
        this.set(key, value, expirationTime, TimeUnit.MILLISECONDS);
    }

    /**
     * Increments a value in Redis by a specified amount.
     *
     * @param key the key of the value to increment
     * @param step the amount to increment the value by
     */
    public void increment(String key, int step) {
        redisTemplate.opsForValue().increment(key, step);
    }

    /**
     * Deletes a value from Redis by its key.
     *
     * @param key the key of the value to delete
     */
    public void del(String key) {
        redisTemplate.delete(key);
    }

    /**
     * Deletes a cached captcha registration by email.
     *
     * @param email the email associated with the captcha registration
     */
    @CacheEvict(value = CONSTANT.CACHE_NAME.CAPTCHA_REGISTER, key = "#email")
    public void delCaptchaRegister(String email) {}

    /**
     * Deletes a cached user token by email.
     *
     * @param email the email associated with the user token
     */
    @CacheEvict(value = CONSTANT.CACHE_NAME.USER_TOKEN, key = "#email")
    public void delUserToken(String email) {}

    /**
     * Deletes a cached user by email.
     *
     * @param email the email associated with the user
     */
    @CacheEvict(value = CONSTANT.CACHE_NAME.USER_CACHE, key = "#email")
    public void delUserCache(String email) {}
}
