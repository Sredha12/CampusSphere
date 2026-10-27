package com.campussphere.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Utility for managing user authentication sessions.
 * <p>
 * Enforces session regeneration upon login to eliminate Session Fixation vulnerabilities,
 * and maintains minimal safe session attributes (userId, userRole, userName).
 */
public final class SessionUtil {

    public static final String ATTR_USER_ID = "userId";
    public static final String ATTR_USER_ROLE = "userRole";
    public static final String ATTR_USER_NAME = "userName";

    public static final String ROLE_STUDENT = "STUDENT";
    public static final String ROLE_ADMIN = "ADMIN";

    private SessionUtil() {
        // Prevent instantiation
    }

    /**
     * Authenticates a student, regenerates the session to prevent fixation,
     * stores safe identity attributes, and generates a fresh CSRF token.
     *
     * @param request the current request
     * @param id      student ID
     * @param name    student full name
     */
    public static void loginStudent(HttpServletRequest request, int id, String name) {
        regenerateAndBind(request, id, ROLE_STUDENT, name);
    }

    /**
     * Authenticates an administrator, regenerates the session to prevent fixation,
     * stores safe identity attributes, and generates a fresh CSRF token.
     *
     * @param request the current request
     * @param id      admin ID
     * @param name    admin full name
     */
    public static void loginAdmin(HttpServletRequest request, int id, String name) {
        regenerateAndBind(request, id, ROLE_ADMIN, name);
    }

    private static void regenerateAndBind(HttpServletRequest request, int id, String role, String name) {
        // Invalidate old session if present
        HttpSession oldSession = request.getSession(false);
        if (oldSession != null) {
            oldSession.invalidate();
        }

        // Create clean fresh session
        HttpSession newSession = request.getSession(true);
        newSession.setAttribute(ATTR_USER_ID, id);
        newSession.setAttribute(ATTR_USER_ROLE, role);
        newSession.setAttribute(ATTR_USER_NAME, (name != null) ? name.trim() : "");

        // Generate a fresh CSRF token for the new session
        CsrfUtil.generateNewToken(newSession);
    }

    /**
     * Logs out the user by invalidating any existing session.
     *
     * @param request the current request
     */
    public static void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    public static Integer getUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        Object id = session.getAttribute(ATTR_USER_ID);
        return (id instanceof Integer) ? (Integer) id : null;
    }

    public static String getUserRole(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        Object role = session.getAttribute(ATTR_USER_ROLE);
        return (role instanceof String) ? (String) role : null;
    }

    public static String getUserName(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        Object name = session.getAttribute(ATTR_USER_NAME);
        return (name instanceof String) ? (String) name : null;
    }

    public static boolean isLoggedIn(HttpServletRequest request) {
        return getUserId(request) != null && getUserRole(request) != null;
    }

    public static boolean isStudent(HttpServletRequest request) {
        return ROLE_STUDENT.equals(getUserRole(request));
    }

    public static boolean isAdmin(HttpServletRequest request) {
        return ROLE_ADMIN.equals(getUserRole(request));
    }
}
