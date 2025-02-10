package com.framework.simpleLogin.service;

import com.framework.simpleLogin.utils.CONSTANT;
import com.framework.simpleLogin.utils.Gadget;
import com.framework.simpleLogin.utils.RedisUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class LoginAttemptService {
    @Resource
    private RedisUtil redisUtil;

    private static final String LOGIN_ATTEMPT_KEY = CONSTANT.CACHE_NAME.USER_ATTEMPT + ":";

    public boolean isLocked(String username) {
        String attempts = (String) redisUtil.get(LOGIN_ATTEMPT_KEY + username);

        return !Gadget.StringUtils.isEmpty(attempts) &&
                Integer.parseInt(attempts) >= CONSTANT.OTHER.LOGIN_FAILURE_LIMIT;
    }

    public int getAttempts(String username) {
        return Integer.parseInt((String) redisUtil.get(LOGIN_ATTEMPT_KEY + username));
    }

    public void failed(String username) {
        String key = LOGIN_ATTEMPT_KEY + username;
        String attempts = (String) redisUtil.get(key);

        if (Gadget.StringUtils.isEmpty(attempts)) {
            redisUtil.set(key, "1", CONSTANT.CACHE_EXPIRATION_TIME.ACCOUNT_LOCKED, TimeUnit.MILLISECONDS);
        } else {
            redisUtil.increment(key, 1);
        }
    }

    public void reset(String username) {
        redisUtil.del(LOGIN_ATTEMPT_KEY + username);
    }
}
