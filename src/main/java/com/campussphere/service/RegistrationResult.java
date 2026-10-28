package com.campussphere.service;

import java.io.Serializable;

/**
 * Result object for event registration and cancellation operations.
 */
public class RegistrationResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private final boolean success;
    private final String message;
    private final String eventTitle;

    private RegistrationResult(boolean success, String message, String eventTitle) {
        this.success = success;
        this.message = message;
        this.eventTitle = eventTitle;
    }

    public static RegistrationResult success(String message, String eventTitle) {
        return new RegistrationResult(true, message, eventTitle);
    }

    public static RegistrationResult error(String message) {
        return new RegistrationResult(false, message, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getEventTitle() {
        return eventTitle;
    }
}
