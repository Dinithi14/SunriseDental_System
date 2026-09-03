package com.sunrisedental.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility for secure password hashing using SHA-256 with cryptographic salt.
 */
public class PasswordUtil {

    private static final String FIXED_APP_SALT = "sunrise_salt_2026";

    /**
     * Generates a random cryptographic salt.
     */
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * Hashes a password string with a specific salt using SHA-256.
     */
    public static String hashPassword(String password, String salt) {
        if (password == null) return null;
        if (salt == null) salt = FIXED_APP_SALT;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes(StandardCharsets.UTF_8));
            byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));
            
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedPassword) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available in environment", e);
        }
    }

    /**
     * Verifies if raw password matches stored hash and salt.
     */
    public static boolean verifyPassword(String rawPassword, String storedHash, String salt) {
        if (rawPassword == null || storedHash == null) return false;
        String calculated = hashPassword(rawPassword, salt);
        return calculated.equalsIgnoreCase(storedHash);
    }
}
