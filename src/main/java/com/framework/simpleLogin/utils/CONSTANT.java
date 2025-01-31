package com.framework.simpleLogin.utils;

import java.util.List;

public final class CONSTANT {
    public static final class CACHE_NAME {
        public static final String CAPTCHA = "captcha";
        public static final String USER = "user";
        public static final String API = "api";

        public static final String CAPTCHA_REGISTER = CAPTCHA + ":register";

        public static final String USER_CACHE = USER + ":cache";
        public static final String USER_TOKEN = USER + ":token";
        public static final String USER_ATTEMPT = USER + ":attempt";

        public static final String API_DEBOUNCE = API + ":debounce";
    }

    public static final class CACHE_EXPIRATION_TIME {
        public static final long USER_TOKEN = 7 * 24 * 60 * 60 * 1000;
        public static final long USER_CACHE = 7 * 24 * 60 * 60 * 1000;
        public static final long LOCK_TIME = 5 * 60 * 1000;
        public static final long API_DEBOUNCE_TIME = 60 * 1000;
        public static final long CAPTCHA = 30 * 60 * 1000;
    }

    public static final class OTHER {
        public static final String AUTHORIZATION_PREFIX = "Bearer ";
        public static final int MAX_PASSWORD_ATTEMPT = 5;
        public static final List<String> ALLOWED_PATH = List.of(
                "/exceptions"
        );
    }
}
