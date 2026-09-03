package com.sunrisedental;

import com.sunrisedental.util.ValidationUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ValidationUtilTest {

    @Test
    @DisplayName("Test Sri Lankan Phone Number Validation")
    public void testPhoneNumberValidation() {
        assertTrue(ValidationUtil.isValidPhoneNumber("0771234567"));
        assertTrue(ValidationUtil.isValidPhoneNumber("0719876543"));
        assertTrue(ValidationUtil.isValidPhoneNumber("+94771234567"));
        assertTrue(ValidationUtil.isValidPhoneNumber("076-5551234"));

        assertFalse(ValidationUtil.isValidPhoneNumber("12345"));
        assertFalse(ValidationUtil.isValidPhoneNumber("0112345678")); // Landline not mobile
        assertFalse(ValidationUtil.isValidPhoneNumber(""));
        assertFalse(ValidationUtil.isValidPhoneNumber(null));
    }

    @Test
    @DisplayName("Test Email Address Validation")
    public void testEmailValidation() {
        assertTrue(ValidationUtil.isValidEmail("patient@gmail.com"));
        assertTrue(ValidationUtil.isValidEmail("dr.perera@sunrisedental.lk"));
        assertTrue(ValidationUtil.isValidEmail("")); // Optional email allowed

        assertFalse(ValidationUtil.isValidEmail("invalid-email-format"));
        assertFalse(ValidationUtil.isValidEmail("@missingusername.com"));
    }

    @Test
    @DisplayName("Test Appointment Number Pattern Validation")
    public void testAppointmentNumberValidation() {
        assertTrue(ValidationUtil.isValidAppointmentNumber("APT-2026-0001"));
        assertTrue(ValidationUtil.isValidAppointmentNumber("APT-2026-1234"));

        assertFalse(ValidationUtil.isValidAppointmentNumber("APT-26-1"));
        assertFalse(ValidationUtil.isValidAppointmentNumber("12345"));
        assertFalse(ValidationUtil.isValidAppointmentNumber(null));
    }

    @Test
    @DisplayName("Test HTML Sanitization")
    public void testSanitization() {
        String dirty = "<script>alert('xss')</script>John Doe";
        String clean = ValidationUtil.sanitize(dirty);
        assertFalse(clean.contains("<"));
        assertFalse(clean.contains(">"));
    }
}
