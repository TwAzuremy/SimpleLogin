package com.framework.simpleLogin.utils;

public class SimpleUtils {
    public static final String authorizationPrefix = "Bearer ";

    public static boolean stringIsEmpty(String str) {
        return str == null || str.isEmpty();
    }
}
