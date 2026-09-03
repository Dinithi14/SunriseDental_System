package com.sunrisedental;

import com.sunrisedental.util.PasswordUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PasswordUtilTest {

    @Test
    @DisplayName("Test SHA-256 Hash Generation & Verification")
    public void testHashAndVerify() {
        String raw = "admin123";
        String salt = "sunrise_salt_2026";

        String hash = PasswordUtil.hashPassword(raw, salt);
        assertNotNull(hash);
        assertEquals(64, hash.length()); // SHA-256 produces 64 hex characters

        assertTrue(PasswordUtil.verifyPassword("admin123", hash, salt));
        assertFalse(PasswordUtil.verifyPassword("wrongpass", hash, salt));
    }

    @Test
    @DisplayName("Test Unique Random Salt Generation")
    public void testSaltGeneration() {
        String salt1 = PasswordUtil.generateSalt();
        String salt2 = PasswordUtil.generateSalt();

        assertNotNull(salt1);
        assertNotNull(salt2);
        assertNotEquals(salt1, salt2);
    }
}
