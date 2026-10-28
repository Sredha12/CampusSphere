package com.campussphere.service;

import com.campussphere.config.AppConfig;
import com.campussphere.dao.RegistrationDAO;
import com.campussphere.model.AvailabilityStatus;
import com.campussphere.model.EventRegistration;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Business service handling event registration, cancellation, and student registration status.
 */
public class RegistrationService {

    private final RegistrationDAO registrationDAO;

    public RegistrationService() {
        this.registrationDAO = new RegistrationDAO();
    }

    public RegistrationService(RegistrationDAO registrationDAO) {
        this.registrationDAO = registrationDAO;
    }

    /**
     * Attempts to register a student for an event in an ACID transaction with pessimistic locking.
     *
     * @param studentId the student ID
     * @param eventId   the event ID
     * @return registration result containing outcome status and friendly user message
     */
    public RegistrationResult registerStudent(int studentId, int eventId) {
        ZoneId zoneId = AppConfig.getInstance().getZoneId();
        return registrationDAO.registerStudent(studentId, eventId, zoneId);
    }

    /**
     * Attempts to cancel a student's active event registration.
     * Disallows cancellation if the event has already started or ended.
     *
     * @param studentId the student ID
     * @param eventId   the event ID
     * @return registration result containing outcome status and friendly user message
     */
    public RegistrationResult cancelRegistration(int studentId, int eventId) {
        ZoneId zoneId = AppConfig.getInstance().getZoneId();
        return registrationDAO.cancelRegistration(studentId, eventId, zoneId);
    }

    /**
     * Checks if a student is actively registered for an event.
     *
     * @param studentId the student ID
     * @param eventId   the event ID
     * @return true if currently actively registered (status = 'REGISTERED')
     */
    public boolean isStudentRegistered(int studentId, int eventId) {
        return registrationDAO.isStudentRegistered(studentId, eventId);
    }

    /**
     * Retrieves an individual registration for a student and event, enriched with event status.
     *
     * @param studentId the student ID
     * @param eventId   the event ID
     * @return enriched EventRegistration, or null if not found
     */
    public EventRegistration getRegistration(int studentId, int eventId) {
        EventRegistration reg = registrationDAO.findRegistration(studentId, eventId);
        if (reg != null) {
            enrichRegistrationStatus(reg);
        }
        return reg;
    }

    /**
     * Retrieves all registrations for a student, enriched with availability status.
     *
     * @param studentId the student ID
     * @return list of event registrations
     */
    public List<EventRegistration> getStudentRegistrations(int studentId) {
        List<EventRegistration> list = registrationDAO.findRegistrationsByStudentId(studentId);
        list.forEach(this::enrichRegistrationStatus);
        return list;
    }

    /**
     * Enriches registration with the target event's current availability state.
     */
    public void enrichRegistrationStatus(EventRegistration reg) {
        if (reg == null) return;

        ZoneId zoneId = AppConfig.getInstance().getZoneId();
        ZonedDateTime now = ZonedDateTime.now(zoneId);

        // 1. Check if event ended
        if (reg.getEventDate() != null) {
            LocalTime finishTime = reg.getEndTime() != null ? reg.getEndTime() : reg.getStartTime();
            if (finishTime != null) {
                ZonedDateTime eventEnd = LocalDateTime.of(reg.getEventDate(), finishTime).atZone(zoneId);
                if (now.isAfter(eventEnd)) {
                    reg.setAvailability(AvailabilityStatus.ENDED);
                    reg.setAvailabilityLabel("Event Ended");
                    return;
                }
            }
        }

        // 2. Check if registration deadline passed
        if (reg.getRegistrationDeadline() != null) {
            ZonedDateTime deadline = reg.getRegistrationDeadline().atZone(zoneId);
            if (now.isAfter(deadline)) {
                reg.setAvailability(AvailabilityStatus.CLOSED);
                reg.setAvailabilityLabel("Registration Closed");
                return;
            }
        }

        // 3. Default to Open
        reg.setAvailability(AvailabilityStatus.OPEN);
        reg.setAvailabilityLabel("Open");
    }

    /**
     * Counts all active registrations (status = 'REGISTERED').
     */
    public int getActiveRegistrationCount() {
        return registrationDAO.countByStatus(EventRegistration.STATUS_REGISTERED);
    }

    /**
     * Counts all cancelled registrations (status = 'CANCELLED').
     */
    public int getCancelledRegistrationCount() {
        return registrationDAO.countByStatus(EventRegistration.STATUS_CANCELLED);
    }

    /**
     * Retrieves the most recent registrations for dashboard display.
     */
    public List<EventRegistration> getRecentRegistrations(int limit) {
        return registrationDAO.findRecentRegistrations(limit);
    }

    /**
     * Retrieves registrations filtered by optional event ID and/or status.
     */
    public List<EventRegistration> getRegistrations(Integer eventId, String status, int limit) {
        return registrationDAO.findRegistrations(eventId, status, limit);
    }

    /**
     * Counts total registrations matching optional filters.
     */
    public int countRegistrations(Integer eventId, String status) {
        return registrationDAO.countRegistrations(eventId, status);
    }

    /**
     * Retrieves all active attendees for an event roster.
     */
    public List<EventRegistration> getEventAttendees(int eventId) {
        return registrationDAO.findAttendeesByEventId(eventId);
    }
}
