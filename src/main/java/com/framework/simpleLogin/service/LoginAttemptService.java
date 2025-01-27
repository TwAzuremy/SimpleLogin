package com.framework.simpleLogin.service;

import com.framework.simpleLogin.utils.CACHE_NAME;
import com.framework.simpleLogin.utils.SimpleUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class LoginAttemptService {
    @Resource
    private RedisService redisService;

    private static final String LOGIN_ATTEMPT_KEY = CACHE_NAME.USER + ":attempt:";
    private static final int MAX_ATTEMPTS = 5;
    private static final int LOCK_TIME = 5 * 60;

    public boolean isLocked(String email) {
        String attempts = (String) redisService.get(LOGIN_ATTEMPT_KEY + email);

        return !SimpleUtils.stringIsEmpty(attempts) && Integer.parseInt(attempts) >= MAX_ATTEMPTS;
    }

    public void failed(String email) {
        String key = LOGIN_ATTEMPT_KEY + email;
        String attempts = (String) redisService.get(key);

        if (SimpleUtils.stringIsEmpty(attempts)) {
            redisService.set(key, "1", LOCK_TIME, TimeUnit.MINUTES);
        } else {
            redisService.increment(key, 1);
        }
    }

    public void reset(String email) {
        redisService.delete(LOGIN_ATTEMPT_KEY + email);
    }
}
