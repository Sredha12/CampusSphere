package com.campussphere.model;

import java.sql.Timestamp;

/**
 * Model representing an Administrator in the system.
 * <p>
 * NOTE: For security, the password hash must NEVER be exposed in request
 * attributes, logs, or forwarded to JSP views.
 */
public class Admin {

    private int id;
    private String fullName;
    private String email;
    private String passwordHash;
    private Timestamp createdAt;

    public Admin() {
    }

    public Admin(int id, String fullName, String email, String passwordHash, Timestamp createdAt) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Internal getter for authentication verification only.
     * Never forward to JSPs or log.
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Admin{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
