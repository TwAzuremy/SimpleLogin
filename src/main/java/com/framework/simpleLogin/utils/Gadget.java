package com.framework.simpleLogin.utils;

import java.util.HashMap;
import java.util.Map;

public final class Gadget {
    public static final class StringUtils {
        public static boolean isEmpty(String str) {
            return str == null || str.trim().isEmpty();
        }

        public static Map<String, String> separateCiphertext(String password) {
            Map<String, String> map = new HashMap<>();

            String cipherText = password.substring(0, 64);
            String salt = password.substring(64, 96);

            map.put("ciphertext", cipherText);
            map.put("salt", salt);

            return map;
        }
    }

    public static String requestTokenProcessing(String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }

        return token.substring(CONSTANT.OTHER.AUTHORIZATION_PREFIX.length());
    }
}
