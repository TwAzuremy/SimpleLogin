package com.framework.simpleLogin.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HexFormat;

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

    /**
     * Generates a HMAC-SHA256 hash of the input data string using a secret key.
     *
     * @param data the input data string to be hashed
     * @return a hexadecimal string representation of the HMAC-SHA256 hash
     * @throws Exception if an error occurs during hash generation
     */
    public static String hmacSHA256(String data) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec("Azuremy".getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] signBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

        return HexFormat.of().formatHex(signBytes);
    }
}
