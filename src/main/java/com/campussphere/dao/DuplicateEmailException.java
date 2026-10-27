package com.campussphere.dao;

/**
 * Unchecked exception thrown when an insert or update violates a UNIQUE email constraint.
 * Identified by MySQL vendor error code 1062 (ER_DUP_ENTRY).
 */
public class DuplicateEmailException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DuplicateEmailException(String message) {
        super(message);
    }

    public DuplicateEmailException(String message, Throwable cause) {
        super(message, cause);
    }
}
