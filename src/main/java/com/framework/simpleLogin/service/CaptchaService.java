package com.framework.simpleLogin.service;

import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class CaptchaService {
    @Resource(name = "stringRedisTemplate")
    private StringRedisTemplate redisTemplate;

    private final Logger logger = LoggerFactory.getLogger(CaptchaService.class);

    /**
     * Simple generation of captcha
     * @param length The length of the captcha
     * @return captcha
     */
    private String randCaptcha(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

        return IntStream.range(0, length)
                .map(i -> ThreadLocalRandom.current().nextInt(chars.length()))
                .mapToObj(i -> String.valueOf(chars.charAt(i)))
                .collect(Collectors.joining());
    }

    public String generator(int length) {
        return randCaptcha(length);
    }

    public void store(String key, String captcha, long expirationTime, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, captcha, expirationTime, timeUnit);

        logger.info("Key '{}' stored verification code '{}' with an expiration date of {} {}.",
                key, captcha, expirationTime, timeUnit.toString().toLowerCase());
    }

    public Boolean verify(String key, String inputCaptcha) {
        String redisCaptcha = redisTemplate.opsForValue().get(key);

        if (redisCaptcha != null) {
            boolean result = redisCaptcha.equals(inputCaptcha.toUpperCase());

            // Remove the captcha after verification
            if (result) {
                logger.info("{} Verified passed.", key);
                redisTemplate.delete(key);
            }

            return result;
        }

        logger.info("The captcha of '{}' is invalid.", key);

        return false;
    }
}
