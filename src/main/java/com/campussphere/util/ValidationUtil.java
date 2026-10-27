package com.campussphere.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Server-side validation utility providing clean field-level validation and sanitization.
 * <p>
 * Methods return field-to-message error maps and never throw exceptions for invalid input.
 */
public final class ValidationUtil {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^\\+?[0-9]{10,15}$");

    private ValidationUtil() {
        // Prevent instantiation
    }

    /**
     * Strips whitespace and dashes from a phone number string.
     *
     * @param phone raw phone string
     * @return cleaned phone string or empty string if null
     */
    public static String cleanPhone(String phone) {
        if (phone == null) {
            return "";
        }
        return phone.replace(" ", "").replace("-", "").trim();
    }

    /**
     * Validates student registration fields.
     *
     * @param fullName        full name (2-100 chars)
     * @param email           email address (max 150 chars, simple format)
     * @param password        password (8-128 chars, at least one letter and one digit)
     * @param confirmPassword must equal password
     * @param department      department name (2-100 chars)
     * @param yearOfStudyStr  year of study (integer 1-5)
     * @param phone           phone number (10-15 digits, optional leading '+')
     * @return map of field name to error message; empty if all valid
     */
    public static Map<String, String> validateStudentRegistration(
            String fullName, String email, String password, String confirmPassword,
            String department, String yearOfStudyStr, String phone) {

        Map<String, String> errors = new LinkedHashMap<>();

        validateFullName(fullName, errors);
        validateEmail(email, errors);
        validatePassword(password, errors);
        validateConfirmPassword(password, confirmPassword, errors);
        validateDepartment(department, errors);
        validateYearOfStudy(yearOfStudyStr, errors);
        validatePhone(phone, errors);

        return errors;
    }

    /**
     * Validates student profile update fields.
     */
    public static Map<String, String> validateStudentProfile(
            String fullName, String department, String yearOfStudyStr, String phone) {

        Map<String, String> errors = new LinkedHashMap<>();

        validateFullName(fullName, errors);
        validateDepartment(department, errors);
        validateYearOfStudy(yearOfStudyStr, errors);
        validatePhone(phone, errors);

        return errors;
    }

    /**
     * Validates admin bootstrap setup fields.
     */
    public static Map<String, String> validateAdminSetup(
            String setupKey, String fullName, String email, String password, String confirmPassword) {

        Map<String, String> errors = new LinkedHashMap<>();

        if (setupKey == null || setupKey.isBlank()) {
            errors.put("setupKey", "Setup key is required.");
        }

        validateFullName(fullName, errors);
        validateEmail(email, errors);
        validatePassword(password, errors);
        validateConfirmPassword(password, confirmPassword, errors);

        return errors;
    }

    public static void validateFullName(String fullName, Map<String, String> errors) {
        if (fullName == null || fullName.trim().length() < 2 || fullName.trim().length() > 100) {
            errors.put("fullName", "Full name must be between 2 and 100 characters.");
        }
    }

    public static void validateEmail(String email, Map<String, String> errors) {
        if (email == null || email.trim().isEmpty()) {
            errors.put("email", "Email address is required.");
            return;
        }
        String trimmed = email.trim();
        if (trimmed.length() > 150) {
            errors.put("email", "Email address cannot exceed 150 characters.");
            return;
        }
        if (!EMAIL_PATTERN.matcher(trimmed).matches()) {
            errors.put("email", "Please provide a valid email address (e.g. user@example.com).");
        }
    }

    public static void validatePassword(String password, Map<String, String> errors) {
        if (password == null || password.length() < 8 || password.length() > 128) {
            errors.put("password", "Password must be between 8 and 128 characters.");
            return;
        }

        boolean hasLetter = false;
        boolean hasDigit = false;
        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) {
                hasLetter = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            }
        }

        if (!hasLetter || !hasDigit) {
            errors.put("password", "Password must contain at least one letter and one digit.");
        }
    }

    public static void validateConfirmPassword(String password, String confirmPassword, Map<String, String> errors) {
        if (confirmPassword == null || !confirmPassword.equals(password)) {
            errors.put("confirmPassword", "Passwords do not match.");
        }
    }

    public static void validateDepartment(String department, Map<String, String> errors) {
        if (department == null || department.trim().length() < 2 || department.trim().length() > 100) {
            errors.put("department", "Department must be between 2 and 100 characters.");
        }
    }

    public static Integer validateYearOfStudy(String yearOfStudyStr, Map<String, String> errors) {
        if (yearOfStudyStr == null || yearOfStudyStr.trim().isEmpty()) {
            errors.put("yearOfStudy", "Year of study is required.");
            return null;
        }
        try {
            int year = Integer.parseInt(yearOfStudyStr.trim());
            if (year < 1 || year > 5) {
                errors.put("yearOfStudy", "Year of study must be between 1 and 5.");
                return null;
            }
            return year;
        } catch (NumberFormatException e) {
            errors.put("yearOfStudy", "Year of study must be a valid number between 1 and 5.");
            return null;
        }
    }

    public static void validatePhone(String phone, Map<String, String> errors) {
        if (phone == null || phone.trim().isEmpty()) {
            errors.put("phone", "Phone number is required.");
            return;
        }
        String cleaned = cleanPhone(phone);
        if (!PHONE_PATTERN.matcher(cleaned).matches()) {
            errors.put("phone", "Phone number must contain 10 to 15 digits (optional leading +).");
        }
    }
}
