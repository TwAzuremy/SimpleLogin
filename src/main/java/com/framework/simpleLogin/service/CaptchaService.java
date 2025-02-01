package com.framework.simpleLogin.service;

import com.framework.simpleLogin.utils.CONSTANT;
import com.framework.simpleLogin.utils.Gadget;
import com.framework.simpleLogin.utils.RedisUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@Service
public class CaptchaService {
    @Resource
    private RedisUtil redisUtil;

    public boolean verify(String CACHE_NAME, String captcha, String email) {
        String key = CACHE_NAME + ":" + email;
        String storedCaptcha = (String) redisUtil.get(key);

        return !Gadget.StringUtils.isEmpty(storedCaptcha) && storedCaptcha.equals(captcha.toUpperCase());
    }

    public String generate(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        SecureRandom random = new SecureRandom();

        return IntStream.range(0, length)
                .map(i -> random.nextInt(chars.length()))
                .mapToObj(chars::charAt)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }

    public String get(String CACHE_NAME, String email) {
        return (String) redisUtil.get(CACHE_NAME + ":" + email);
    }

    public void store(String CACHE_NAME, String email, String captcha) {
        redisUtil.set(CACHE_NAME + ":" + email, captcha, CONSTANT.CACHE_EXPIRATION_TIME.CAPTCHA, TimeUnit.MILLISECONDS);
    }
}
