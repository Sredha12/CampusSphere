package com.campussphere.dao;

/**
 * Exception thrown when a data access or JDBC operation encounters an error.
 * Wraps low-level SQLExceptions to avoid leaking database internals to presentation layers.
 */
public class DataAccessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DataAccessException(String message) {
        super(message);
    }

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
