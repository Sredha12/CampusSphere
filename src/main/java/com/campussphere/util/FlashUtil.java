package com.campussphere.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Utility for managing one-time flash alerts across HTTP redirects (Post/Redirect/Get).
 * Flash messages are placed in session scope and automatically consumed and removed
 * upon rendering in {@code header.jsp}.
 */
public final class FlashUtil {

    public static final String FLASH_SUCCESS = "flashSuccess";
    public static final String FLASH_ERROR = "flashError";

    private FlashUtil() {
        // Prevent instantiation
    }

    /**
     * Sets a one-time success notification message.
     *
     * @param request the current HTTP request
     * @param message user-facing text
     */
    public static void setSuccess(HttpServletRequest request, String message) {
        if (request != null && message != null) {
            HttpSession session = request.getSession(true);
            session.setAttribute(FLASH_SUCCESS, message);
        }
    }

    /**
     * Sets a one-time error notification message.
     *
     * @param request the current HTTP request
     * @param message user-facing text
     */
    public static void setError(HttpServletRequest request, String message) {
        if (request != null && message != null) {
            HttpSession session = request.getSession(true);
            session.setAttribute(FLASH_ERROR, message);
        }
    }
}
