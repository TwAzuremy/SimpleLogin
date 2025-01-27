package com.framework.simpleLogin.service;

import com.framework.simpleLogin.utils.CACHE_NAME;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class CaptchaService {
    @Resource
    private RedisService redisService;

    private final long timeout = 10;

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

    public void store(String key, String captcha) {
        redisService.set(CACHE_NAME.CAPTCHA + ":" + key, captcha, timeout, TimeUnit.MINUTES);
    }

    public Boolean verify(String key, String inputCaptcha) {
        String redisCaptcha = (String) redisService.get(CACHE_NAME.CAPTCHA + ":" + key);

        if (redisCaptcha != null) {
            boolean result = redisCaptcha.equals(inputCaptcha.toUpperCase());

            // Remove the captcha after verification
            if (result) {
                redisService.deleteCaptcha(key);
            }

            return result;
        }

        return false;
    }

    public String isExists(String key) {
        return (String) redisService.get(CACHE_NAME.CAPTCHA + ":" + key);
    }
}
