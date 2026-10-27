package com.campussphere.service;

import com.campussphere.config.AppConfig;
import com.campussphere.dao.AdminDAO;
import com.campussphere.dao.DuplicateEmailException;
import com.campussphere.dao.StudentDAO;
import com.campussphere.model.Admin;
import com.campussphere.model.Student;
import com.campussphere.util.LoginThrottle;
import com.campussphere.util.PasswordUtil;
import com.campussphere.util.ValidationUtil;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service orchestrating authentication, self-service student registration,
 * student profile management, and one-time administrator bootstrap.
 */
public class AuthService {

    private static final Logger LOGGER = Logger.getLogger(AuthService.class.getName());
    public static final String GENERIC_LOGIN_ERROR = "Invalid email or password.";

    private final StudentDAO studentDAO;
    private final AdminDAO adminDAO;

    public AuthService() {
        this(new StudentDAO(), new AdminDAO());
    }

    public AuthService(StudentDAO studentDAO, AdminDAO adminDAO) {
        this.studentDAO = studentDAO;
        this.adminDAO = adminDAO;
    }

    /**
     * Registers a new student account after validating all input fields.
     *
     * @param fullName        full name (2-100 chars)
     * @param email           email address (max 150 chars)
     * @param password        plaintext password
     * @param confirmPassword confirmation password
     * @param department      department name (2-100 chars)
     * @param yearOfStudyStr  year of study (1-5)
     * @param phone           phone number
     * @return AuthResult containing the new student ID on success, or field errors on failure
     */
    public AuthResult<Integer> registerStudent(
            String fullName, String email, String password, String confirmPassword,
            String department, String yearOfStudyStr, String phone) {

        Map<String, String> errors = ValidationUtil.validateStudentRegistration(
                fullName, email, password, confirmPassword, department, yearOfStudyStr, phone);

        if (!errors.isEmpty()) {
            return AuthResult.failure(errors);
        }

        int year = Integer.parseInt(yearOfStudyStr.trim());
        String cleanedPhone = ValidationUtil.cleanPhone(phone);
        String passwordHash = PasswordUtil.hash(password);

        Student student = new Student();
        student.setFullName(fullName != null ? fullName.trim() : "");
        student.setEmail(email != null ? email.trim().toLowerCase() : "");
        student.setPasswordHash(passwordHash);
        student.setDepartment(department != null ? department.trim() : "");
        student.setYearOfStudy(year);
        student.setPhone(cleanedPhone);

        try {
            int newId = studentDAO.insert(student);
            return AuthResult.success(newId);
        } catch (DuplicateEmailException e) {
            Map<String, String> dupErrors = new LinkedHashMap<>();
            dupErrors.put("email", "An account with this email address already exists.");
            return AuthResult.failure(dupErrors);
        }
    }

    /**
     * Authenticates a student using email and password.
     * Enforces rate limiting and uses generic error messages to resist enumeration.
     *
     * @param email    student email
     * @param password candidate password
     * @param clientIp client IP address from request
     * @return AuthResult containing the authenticated Student or an error message
     */
    public AuthResult<Student> authenticateStudent(String email, String password, String clientIp) {
        String throttleKey = LoginThrottle.buildLoginKey(email, clientIp);

        if (LoginThrottle.isBlocked(throttleKey)) {
            return AuthResult.genericFailure(LoginThrottle.THROTTLE_ERROR_MESSAGE);
        }

        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            LoginThrottle.recordFailure(throttleKey);
            return AuthResult.genericFailure(GENERIC_LOGIN_ERROR);
        }

        Student student = studentDAO.findByEmail(email.trim().toLowerCase());

        if (student == null) {
            // Equalize response time by verifying candidate against dummy hash
            PasswordUtil.verifyDummy(password);
            LoginThrottle.recordFailure(throttleKey);
            return AuthResult.genericFailure(GENERIC_LOGIN_ERROR);
        }

        boolean valid = PasswordUtil.verify(password, student.getPasswordHash());
        if (!valid) {
            LoginThrottle.recordFailure(throttleKey);
            return AuthResult.genericFailure(GENERIC_LOGIN_ERROR);
        }

        // Authentication succeeded: clear rate limit counter
        LoginThrottle.recordSuccess(throttleKey);
        return AuthResult.success(student);
    }

    /**
     * Authenticates an administrator using email and password.
     *
     * @param email    admin email
     * @param password candidate password
     * @param clientIp client IP address
     * @return AuthResult containing the authenticated Admin or an error message
     */
    public AuthResult<Admin> authenticateAdmin(String email, String password, String clientIp) {
        String throttleKey = LoginThrottle.buildLoginKey(email, clientIp);

        if (LoginThrottle.isBlocked(throttleKey)) {
            return AuthResult.genericFailure(LoginThrottle.THROTTLE_ERROR_MESSAGE);
        }

        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            LoginThrottle.recordFailure(throttleKey);
            return AuthResult.genericFailure(GENERIC_LOGIN_ERROR);
        }

        Admin admin = adminDAO.findByEmail(email.trim().toLowerCase());

        if (admin == null) {
            PasswordUtil.verifyDummy(password);
            LoginThrottle.recordFailure(throttleKey);
            return AuthResult.genericFailure(GENERIC_LOGIN_ERROR);
        }

        boolean valid = PasswordUtil.verify(password, admin.getPasswordHash());
        if (!valid) {
            LoginThrottle.recordFailure(throttleKey);
            return AuthResult.genericFailure(GENERIC_LOGIN_ERROR);
        }

        LoginThrottle.recordSuccess(throttleKey);
        return AuthResult.success(admin);
    }

    /**
     * Updates an existing student's profile details.
     *
     * @param studentId       primary key of the student
     * @param fullName        updated name
     * @param department      updated department
     * @param yearOfStudyStr  updated year of study (1-5)
     * @param phone           updated phone number
     * @return AuthResult indicating success or field-level validation errors
     */
    public AuthResult<Void> updateStudentProfile(
            int studentId, String fullName, String department, String yearOfStudyStr, String phone) {

        Map<String, String> errors = ValidationUtil.validateStudentProfile(
                fullName, department, yearOfStudyStr, phone);

        if (!errors.isEmpty()) {
            return AuthResult.failure(errors);
        }

        int year = Integer.parseInt(yearOfStudyStr.trim());
        String cleanedPhone = ValidationUtil.cleanPhone(phone);

        boolean updated = studentDAO.updateProfile(studentId, fullName.trim(), department.trim(), year, cleanedPhone);
        if (updated) {
            return AuthResult.success(null);
        } else {
            return AuthResult.genericFailure("Failed to update profile. Student record not found.");
        }
    }

    /**
     * Creates the initial administrator account during one-time system bootstrap.
     * Uses constant-time SHA-256 digest comparison to verify the setup key.
     *
     * @param candidateKey    the setup key submitted in the form
     * @param fullName        admin full name
     * @param email           admin email
     * @param password        plaintext password
     * @param confirmPassword confirmation password
     * @param clientIp        client IP address
     * @return AuthResult with success or validation/key errors
     */
    public AuthResult<Void> createFirstAdmin(
            String candidateKey, String fullName, String email,
            String password, String confirmPassword, String clientIp) {

        String throttleKey = LoginThrottle.buildSetupKey(clientIp);

        if (LoginThrottle.isBlocked(throttleKey)) {
            return AuthResult.genericFailure(LoginThrottle.THROTTLE_ERROR_MESSAGE);
        }

        AppConfig config = AppConfig.getInstance();
        if (!config.isSetupKeyConfigured()) {
            LOGGER.log(Level.WARNING, "Admin setup attempt blocked: setup key is not configured.");
            return AuthResult.genericFailure("Setup is currently unavailable.");
        }

        Map<String, String> errors = ValidationUtil.validateAdminSetup(
                candidateKey, fullName, email, password, confirmPassword);

        if (!errors.isEmpty()) {
            return AuthResult.failure(errors);
        }

        // Verify setup key using constant-time comparison of SHA-256 digests
        boolean keyMatches = compareKeyDigests(candidateKey, config.getSetupKey());
        if (!keyMatches) {
            LoginThrottle.recordFailure(throttleKey);
            Map<String, String> keyErrors = new LinkedHashMap<>();
            keyErrors.put("setupKey", "Invalid setup key.");
            return AuthResult.failure(keyErrors);
        }

        String passwordHash = PasswordUtil.hash(password);
        boolean inserted = adminDAO.insertIfNoAdminExists(
                fullName.trim(), email.trim().toLowerCase(), passwordHash);

        if (!inserted) {
            // An admin was already created by another request
            return AuthResult.genericFailure("An administrator account already exists. Setup is locked.");
        }

        LoginThrottle.recordSuccess(throttleKey);
        return AuthResult.success(null);
    }

    /**
     * Compares SHA-256 digests of two strings in constant time.
     */
    private boolean compareKeyDigests(String candidateKey, String configuredKey) {
        if (candidateKey == null || configuredKey == null) {
            return false;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] candidateDigest = md.digest(candidateKey.trim().getBytes(StandardCharsets.UTF_8));
            byte[] configuredDigest = md.digest(configuredKey.trim().getBytes(StandardCharsets.UTF_8));
            return MessageDigest.isEqual(candidateDigest, configuredDigest);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.log(Level.SEVERE, "SHA-256 message digest algorithm not found", e);
            throw new IllegalStateException("Crypto digest unavailable", e);
        }
    }

    /**
     * Lightweight generic container for authentication and validation operation outcomes.
     *
     * @param <T> payload type
     */
    public static class AuthResult<T> {
        private final boolean success;
        private final T data;
        private final Map<String, String> fieldErrors;
        private final String genericError;

        private AuthResult(boolean success, T data, Map<String, String> fieldErrors, String genericError) {
            this.success = success;
            this.data = data;
            this.fieldErrors = fieldErrors != null ? fieldErrors : Collections.emptyMap();
            this.genericError = genericError;
        }

        public static <T> AuthResult<T> success(T data) {
            return new AuthResult<>(true, data, null, null);
        }

        public static <T> AuthResult<T> failure(Map<String, String> fieldErrors) {
            return new AuthResult<>(false, null, fieldErrors, null);
        }

        public static <T> AuthResult<T> genericFailure(String message) {
            return new AuthResult<>(false, null, null, message);
        }

        public boolean isSuccess() {
            return success;
        }

        public T getData() {
            return data;
        }

        public Map<String, String> getFieldErrors() {
            return fieldErrors;
        }

        public String getGenericError() {
            return genericError;
        }
    }
}
