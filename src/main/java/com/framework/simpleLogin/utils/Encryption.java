package com.framework.simpleLogin.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class Encryption {
    private static String byteToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder();

        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }

            hexString.append(hex);
        }

        return hexString.toString();
    }

    /**
     * Normal SHA-256 encryption.
     * @param plaintext Text that needs to be encrypted, such as your password.
     * @return Encrypted text, length: 64
     */
    public static String SHA256(String plaintext) {
        if (plaintext != null && !plaintext.isEmpty()) {
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                digest.update(plaintext.getBytes());

                return byteToHex(digest.digest());
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }

        return plaintext;
    }

    /**
     * A 32-bit salt is generated.
     * @return salt, length: 32
     */
    public static String generateSalt() {
        SecureRandom rand = new SecureRandom();
        byte[] salt = new byte[16];
        rand.nextBytes(salt);

        return byteToHex(salt);
    }
}
