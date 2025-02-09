package com.framework.simpleLogin.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

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

        public static String signatureStringFormat(String method, String path, String query, String body, String timestamp, String nonce) {
            query = Gadget.StringUtils.isEmpty(query) ? "" : query + "&";
            body = Gadget.StringUtils.isEmpty(body) ? "" : "body=" + body + "&";

            return method + path + "?" + query + body + "timestamp=" + timestamp + "&nonce=" + nonce;
        }

        public static String hideSensitive(String hasSensitive) {
            return hasSensitive.replaceAll(
                    "(?i)(\"?password\"?\\s*[:=]\\s*)([\"']?)([^&\"'\\s]+?)([\"']?)(?=&|\\s|$|\\b)",
                    "$1$2***$4"
            );
        }

        public static String format(String template, String... values) {
            for (String value : values) {
                template = template.replaceFirst("\\{}", value);
            }

            return template;
        }
    }

    public static String requestTokenProcessing(String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }

        return token.substring(CONSTANT.OTHER.AUTHORIZATION_PREFIX.length());
    }
}
