package com.sunrisedental.util;

import java.util.regex.Pattern;

/**
 * Utility class for rigorous input verification, data cleaning, and business rule validation.
 */
public class ValidationUtil {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^(\\+94|0)?7[0-9]{8}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern APPOINTMENT_NO_PATTERN = Pattern.compile("^APT-\\d{4}-\\d{4}$");

    public static boolean isValidPhoneNumber(String phone) {
        if (phone == null || phone.trim().isEmpty()) return false;
        String clean = phone.replaceAll("[\\s-]", "");
        return PHONE_PATTERN.matcher(clean).matches();
    }

    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) return true; // Email can be optional
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidAppointmentNumber(String appNumber) {
        if (appNumber == null || appNumber.trim().isEmpty()) return false;
        return APPOINTMENT_NO_PATTERN.matcher(appNumber.trim()).matches();
    }

    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static String sanitize(String input) {
        if (input == null) return "";
        return input.trim().replaceAll("[<>\"']", "");
    }
}
