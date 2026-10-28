package com.campussphere.util;

import com.campussphere.config.AppConfig;
import com.campussphere.dao.ClubDAO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
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

    /**
     * Validates an optional local image URL.
     */
    public static void validateImageUrl(String imageUrl, Map<String, String> errors) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            return; // Optional
        }
        String trimmed = imageUrl.trim();
        if (trimmed.length() > 255) {
            errors.put("imageUrl", "Image path cannot exceed 255 characters.");
            return;
        }
        if (!trimmed.startsWith("assets/") && !trimmed.startsWith("/")) {
            errors.put("imageUrl", "Image URL must be a local path starting with 'assets/' or '/'.");
            return;
        }
        if (trimmed.contains("..") || trimmed.contains("://") || trimmed.toLowerCase().contains("javascript:")
                || trimmed.matches(".*\\s+.*")) {
            errors.put("imageUrl", "Image path contains invalid characters, whitespace, or external URL protocols.");
        }
    }

    /**
     * Validates club fields for creation and updates.
     */
    public static Map<String, String> validateClub(
            String name, String description, String contactEmail, String imageUrl,
            Integer excludeClubId, ClubDAO clubDAO) {

        Map<String, String> errors = new LinkedHashMap<>();

        if (name == null || name.trim().isEmpty()) {
            errors.put("name", "Club name is required.");
        } else {
            String trimmed = name.trim();
            if (trimmed.length() > 100) {
                errors.put("name", "Club name cannot exceed 100 characters.");
            } else if (clubDAO != null && clubDAO.existsByName(trimmed, excludeClubId)) {
                errors.put("name", "A club with this name already exists. Please choose a unique name.");
            }
        }

        if (description == null || description.trim().isEmpty()) {
            errors.put("description", "Club description is required.");
        } else if (description.trim().length() > 2000) {
            errors.put("description", "Club description cannot exceed 2000 characters.");
        }

        if (contactEmail != null && !contactEmail.trim().isEmpty()) {
            String trimmedEmail = contactEmail.trim();
            if (trimmedEmail.length() > 150) {
                errors.put("contactEmail", "Contact email cannot exceed 150 characters.");
            } else if (!EMAIL_PATTERN.matcher(trimmedEmail).matches()) {
                errors.put("contactEmail", "Please provide a valid email address (e.g. club@example.com).");
            }
        }

        validateImageUrl(imageUrl, errors);

        return errors;
    }

    /**
     * Validates event fields for creation and updates.
     */
    public static Map<String, String> validateEvent(
            Integer clubId, String title, String description, String category, String venue,
            String eventDateStr, String startTimeStr, String endTimeStr, String deadlineStr,
            String capacityStr, String imageUrl, Integer currentRegisteredCount, ClubDAO clubDAO) {

        Map<String, String> errors = new LinkedHashMap<>();

        if (clubId == null || clubId <= 0 || (clubDAO != null && clubDAO.findById(clubId) == null)) {
            errors.put("clubId", "Please select a valid hosting club.");
        }

        if (title == null || title.trim().isEmpty()) {
            errors.put("title", "Event title is required.");
        } else if (title.trim().length() > 150) {
            errors.put("title", "Event title cannot exceed 150 characters.");
        }

        if (description == null || description.trim().isEmpty()) {
            errors.put("description", "Event description is required.");
        } else if (description.trim().length() > 4000) {
            errors.put("description", "Event description cannot exceed 4000 characters.");
        }

        if (category == null || category.trim().isEmpty()) {
            errors.put("category", "Event category is required.");
        } else if (category.trim().length() > 50) {
            errors.put("category", "Event category cannot exceed 50 characters.");
        }

        if (venue == null || venue.trim().isEmpty()) {
            errors.put("venue", "Event venue is required.");
        } else if (venue.trim().length() > 150) {
            errors.put("venue", "Event venue cannot exceed 150 characters.");
        }

        LocalDate eventDate = null;
        if (eventDateStr == null || eventDateStr.trim().isEmpty()) {
            errors.put("eventDate", "Event date is required.");
        } else {
            try {
                eventDate = LocalDate.parse(eventDateStr.trim());
            } catch (DateTimeParseException e) {
                errors.put("eventDate", "Event date must be a valid date (YYYY-MM-DD).");
            }
        }

        LocalTime startTime = null;
        if (startTimeStr == null || startTimeStr.trim().isEmpty()) {
            errors.put("startTime", "Start time is required.");
        } else {
            try {
                startTime = LocalTime.parse(startTimeStr.trim());
            } catch (DateTimeParseException e) {
                errors.put("startTime", "Start time must be a valid time (HH:MM).");
            }
        }

        LocalTime endTime = null;
        if (endTimeStr == null || endTimeStr.trim().isEmpty()) {
            errors.put("endTime", "End time is required.");
        } else {
            try {
                endTime = LocalTime.parse(endTimeStr.trim());
            } catch (DateTimeParseException e) {
                errors.put("endTime", "End time must be a valid time (HH:MM).");
            }
        }

        if (startTime != null && endTime != null) {
            if (!endTime.isAfter(startTime)) {
                errors.put("endTime", "End time must be after the start time.");
            }
        }

        LocalDateTime deadline = null;
        if (deadlineStr == null || deadlineStr.trim().isEmpty()) {
            errors.put("registrationDeadline", "Registration deadline is required.");
        } else {
            try {
                deadline = LocalDateTime.parse(deadlineStr.trim());
            } catch (DateTimeParseException e) {
                errors.put("registrationDeadline", "Registration deadline must be a valid date and time.");
            }
        }

        if (eventDate != null && startTime != null && deadline != null) {
            LocalDateTime eventStart = LocalDateTime.of(eventDate, startTime);
            if (deadline.isAfter(eventStart)) {
                errors.put("registrationDeadline", "Registration deadline cannot be after the event start time.");
            }
        }

        if (capacityStr != null && !capacityStr.trim().isEmpty()) {
            try {
                int capacity = Integer.parseInt(capacityStr.trim());
                if (capacity <= 0) {
                    errors.put("capacity", "Capacity must be greater than zero, or leave empty for unlimited.");
                } else if (capacity > 100000) {
                    errors.put("capacity", "Capacity cannot exceed 100,000 attendees.");
                } else if (currentRegisteredCount != null && capacity < currentRegisteredCount) {
                    errors.put("capacity", "Capacity cannot be less than the current number of registered attendees (" +
                            currentRegisteredCount + ").");
                }
            } catch (NumberFormatException e) {
                errors.put("capacity", "Capacity must be a valid integer number.");
            }
        }

        validateImageUrl(imageUrl, errors);

        return errors;
    }
}
