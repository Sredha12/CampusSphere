package com.campussphere.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Cross-Site Request Forgery (CSRF) protection utility.
 * <p>
 * Generates and validates cryptographic, URL-safe Base64 session tokens (32 bytes).
 * Comparison is strictly performed using {@link MessageDigest#isEqual} in constant time.
 */
public final class CsrfUtil {

    public static final String SESSION_ATTR = "csrfToken";
    public static final String PARAM_NAME = "_csrf";
    public static final String REQ_ATTR = "csrfToken";

    private static final int TOKEN_BYTE_LENGTH = 32;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private CsrfUtil() {
        // Prevent instantiation
    }

    /**
     * Lazily retrieves the current CSRF token from the session or generates a new one.
     *
     * @param session the user's HTTP session
     * @return 32-byte URL-safe Base64 token
     */
    public static String getOrCreateToken(HttpSession session) {
        if (session == null) {
            return null;
        }

        String token = (String) session.getAttribute(SESSION_ATTR);
        if (token == null || token.isBlank()) {
            token = generateNewToken(session);
        }
        return token;
    }

    /**
     * Generates a brand-new CSRF token and assigns it to the session.
     * Invoked upon authentication to prevent fixation.
     *
     * @param session the target HTTP session
     * @return the newly generated token
     */
    public static String generateNewToken(HttpSession session) {
        if (session == null) {
            return null;
        }

        byte[] randomBytes = new byte[TOKEN_BYTE_LENGTH];
        SECURE_RANDOM.nextBytes(randomBytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        session.setAttribute(SESSION_ATTR, token);
        return token;
    }

    /**
     * Exposes the CSRF token to the request scope so JSPs can access it via {@code ${csrfToken}}.
     *
     * @param request the current HTTP request
     * @return the token string, or null if no session exists
     */
    public static String exposeToken(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        String token = getOrCreateToken(session);
        request.setAttribute(REQ_ATTR, token);
        return token;
    }

    /**
     * Validates that the submitted parameter matches the session token in constant time.
     *
     * @param request the incoming HTTP request
     * @return true if token is present, matches session token, and session is active; false otherwise
     */
    public static boolean isValid(HttpServletRequest request) {
        if (request == null) {
            return false;
        }

        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }

        String sessionToken = (String) session.getAttribute(SESSION_ATTR);
        if (sessionToken == null || sessionToken.isBlank()) {
            return false;
        }

        String submittedToken = request.getParameter(PARAM_NAME);
        if (submittedToken == null || submittedToken.isBlank()) {
            return false;
        }

        byte[] sessionBytes = sessionToken.getBytes(StandardCharsets.UTF_8);
        byte[] submittedBytes = submittedToken.getBytes(StandardCharsets.UTF_8);

        return MessageDigest.isEqual(sessionBytes, submittedBytes);
    }
}
