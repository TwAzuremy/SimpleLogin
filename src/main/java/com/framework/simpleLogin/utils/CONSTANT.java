package com.framework.simpleLogin.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@PropertySource("classpath:constant.properties")
public final class CONSTANT {
    public static final class CACHE_NAME {
        public static final String CAPTCHA = "captcha";
        public static final String USER = "user";
        public static final String API = "api";
        public static final String OAUTH2 = "oauth2";

        public static final String CAPTCHA_REGISTER = CAPTCHA + ":register";
        public static final String CAPTCHA_MODIFY_PASSWORD = CAPTCHA + ":modify-password";
        public static final String CAPTCHA_RESET_PASSWORD = CAPTCHA + "reset-password";

        public static final String USER_CACHE = USER + ":cache";
        public static final String USER_TOKEN = USER + ":token";
        public static final String USER_ATTEMPT = USER + ":attempt";

        public static final String API_DEBOUNCE = API + ":debounce";
        public static final String API_SIGNATURE = API + ":signature";

        public static final String OAUTH2_STATE = OAUTH2 + ":state";
    }

    @Component
    public static final class CACHE_EXPIRATION_TIME {
        /*
         * Since the default values used in the annotation are used here,
         * it is not possible to assign values dynamically via get.
         */
        public static final long API_DEBOUNCE_TIME = 60 * 1000;

        public static long USER_CACHE;
        public static long USER_TOKEN;
        public static long API_DEBOUNCE;
        public static long ACCOUNT_LOCKED;
        public static long CAPTCHA;
        public static long API_SIGNATURE;
        public static long OAUTH2_STATE;

        public CACHE_EXPIRATION_TIME(
                @Value("${cache.expiration-time.user-cache}") long USER_CACHE,
                @Value("${cache.expiration-time.user-token}") long USER_TOKEN,
                @Value("${cache.expiration-time.api-debounce}") long API_DEBOUNCE,
                @Value("${cache.expiration-time.account-locked}") long ACCOUNT_LOCKED,
                @Value("${cache.expiration-time.captcha}") long CAPTCHA,
                @Value("${cache.expiration-time.api-signature}") long API_SIGNATURE,
                @Value("${cache.expiration-time.oauth2-state}") long OAUTH2_STATE
        ) {
            CACHE_EXPIRATION_TIME.USER_CACHE = USER_CACHE;
            CACHE_EXPIRATION_TIME.USER_TOKEN = USER_TOKEN;
            CACHE_EXPIRATION_TIME.API_DEBOUNCE = API_DEBOUNCE;
            CACHE_EXPIRATION_TIME.ACCOUNT_LOCKED = ACCOUNT_LOCKED;
            CACHE_EXPIRATION_TIME.CAPTCHA = CAPTCHA;
            CACHE_EXPIRATION_TIME.API_SIGNATURE = API_SIGNATURE;
            CACHE_EXPIRATION_TIME.OAUTH2_STATE = OAUTH2_STATE;
        }

        public static long get(String key) {
            return switch (key) {
                case "user-cache" -> USER_CACHE;
                case "user-token" -> USER_TOKEN;
                case "api-debounce" -> API_DEBOUNCE;
                case "account-locked" -> ACCOUNT_LOCKED;
                case "captcha" -> CAPTCHA;
                default -> 0;
            };
        }
    }

    public static final class REGEX {
        public static final String EMAIL = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    }

    @Component
    public static final class OTHER {
        /*
         * Since 'ExceptionFilter' is not in the filter chain of Security,
         * if an exception occurs in the filter,
         * the path forwarded by the 'ExceptionFilter' will pass through the filter chain of Security,
         * resulting in the failure to throw an exception normally.
         */
        public static final List<String> JWT_ALLOWED_PATH = List.of(
                "/exceptions"
        );
        public static final String AUTHORIZATION_PREFIX = "Bearer ";

        public static String CAPTCHA_TEMPLATE;
        public static String REGISTRATION_SUCCESSFUL_TEMPLATE;
        public static int LOGIN_FAILURE_LIMIT;

        public OTHER(
                @Value("${template.html.captcha}") String CAPTCHA_TEMPLATE,
                @Value("${template.html.registration-successful}") String REGISTRATION_SUCCESSFUL_TEMPLATE,
                @Value("${other.attempt.login-failure-limit}") int LOGIN_FAILURE_LIMIT
        ) {
            OTHER.CAPTCHA_TEMPLATE = CAPTCHA_TEMPLATE;
            OTHER.REGISTRATION_SUCCESSFUL_TEMPLATE = REGISTRATION_SUCCESSFUL_TEMPLATE;
            OTHER.LOGIN_FAILURE_LIMIT = LOGIN_FAILURE_LIMIT;
        }

        public static Object get(String key) {
            return switch (key) {
                case "login-failure-limit" -> LOGIN_FAILURE_LIMIT;
                case "captcha" -> CAPTCHA_TEMPLATE;
                default -> null;
            };
        }
    }
}
