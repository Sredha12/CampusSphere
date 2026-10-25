package com.campussphere.model;

import java.io.Serializable;

/**
 * Domain entity representing a college student club or society.
 */
public class Club implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private String description;
    private String contactEmail;
    private String imageUrl;
    private int upcomingEventCount;

    public Club() {
    }

    public Club(int id, String name, String description, String contactEmail, String imageUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.contactEmail = contactEmail;
        this.imageUrl = imageUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * Sanitized image URL: only relative local paths starting with '/' or 'assets/' are permitted.
     */
    public String getSafeImageUrl() {
        if (imageUrl != null && (imageUrl.startsWith("/") || imageUrl.startsWith("assets/"))) {
            return imageUrl;
        }
        return null;
    }

    public int getUpcomingEventCount() {
        return upcomingEventCount;
    }

    public void setUpcomingEventCount(int upcomingEventCount) {
        this.upcomingEventCount = upcomingEventCount;
    }
}
