package com.framework.simpleLogin.service;

import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class CaptchaService {
    @Resource
    private RedisService redisService;

    private final String cacheName = "captcha";

    private final Logger logger = LoggerFactory.getLogger(CaptchaService.class);

    /**
     * Simple generation of captcha
     *
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
        redisService.set(cacheName + ":" + key, captcha, expirationTime, timeUnit);

        logger.info("In the cache name '{}', key '{}' stores the verification code '{}' with an expiration date of {} {}.",
                cacheName, key, captcha, expirationTime, timeUnit.toString().toLowerCase());
    }

    public Boolean verify(String key, String inputCaptcha) {
        String redisCaptcha = (String) redisService.get(cacheName + ":" + key);

        if (redisCaptcha != null) {
            boolean result = redisCaptcha.equals(inputCaptcha.toUpperCase());

            // Remove the captcha after verification
            if (result) {
                logger.info("In the cache name '{}', '{}' Verified passed.", cacheName, key);
                redisService.delete(cacheName, key);
            }

            return result;
        }

        logger.info("The captcha of '{}' is invalid.", key);

        return false;
    }
}
