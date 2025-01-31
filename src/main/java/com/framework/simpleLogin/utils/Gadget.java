package com.framework.simpleLogin.utils;

public final class Gadget {
    public static final class StringUtils {
        public static boolean isEmpty(String str) {
            return str == null || str.trim().isEmpty();
        }
    }

    public static String requestTokenProcessing(String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }

        return token.substring(CONSTANT.OTHER.AUTHORIZATION_PREFIX.length());
    }
}
