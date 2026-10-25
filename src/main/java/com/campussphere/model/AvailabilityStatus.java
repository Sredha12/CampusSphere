package com.campussphere.model;

/**
 * Event registration availability status.
 */
public enum AvailabilityStatus {
    OPEN("Open"),
    FULL("Full"),
    CLOSED("Closed"),
    ENDED("Ended");

    private final String label;

    AvailabilityStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
