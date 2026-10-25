package com.campussphere.service;

import com.campussphere.config.AppConfig;
import com.campussphere.dao.EventDAO;
import com.campussphere.model.AvailabilityStatus;
import com.campussphere.model.Event;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Business service handling event retrieval, filtering, and availability calculations.
 */
public class EventService {

    private final EventDAO eventDAO;

    public EventService() {
        this.eventDAO = new EventDAO();
    }

    public EventService(EventDAO eventDAO) {
        this.eventDAO = eventDAO;
    }

    /**
     * Returns today's date adjusted to the configured application timezone.
     */
    public LocalDate today() {
        return LocalDate.now(AppConfig.getInstance().getZoneId());
    }

    /**
     * Retrieves upcoming events up to the specified limit and calculates their availability.
     */
    public List<Event> getUpcomingEvents(int limit) {
        List<Event> events = eventDAO.findUpcoming(limit);
        events.forEach(this::enrichEventStatus);
        return events;
    }

    /**
     * Searches upcoming events matching category and/or keyword.
     */
    public List<Event> searchEvents(String category, String keyword) {
        List<Event> events = eventDAO.search(category, keyword, today());
        events.forEach(this::enrichEventStatus);
        return events;
    }

    /**
     * Retrieves event details by ID and enriches with availability calculations.
     */
    public Event getEventById(int id) {
        Event event = eventDAO.findById(id);
        if (event != null) {
            enrichEventStatus(event);
        }
        return event;
    }

    /**
     * Retrieves upcoming events for a specific club.
     */
    public List<Event> getEventsByClubId(int clubId) {
        List<Event> events = eventDAO.findByClubId(clubId, today());
        events.forEach(this::enrichEventStatus);
        return events;
    }

    /**
     * Retrieves all distinct event categories.
     */
    public List<String> getCategories() {
        return eventDAO.findCategories();
    }

    /**
     * Calculates derived fields: seatsLeft, availability status, and friendly label.
     */
    public void enrichEventStatus(Event event) {
        if (event == null) return;

        ZoneId zoneId = AppConfig.getInstance().getZoneId();
        ZonedDateTime now = ZonedDateTime.now(zoneId);

        // 1. Calculate seats left
        if (event.getCapacity() != null) {
            int left = Math.max(0, event.getCapacity() - event.getRegisteredCount());
            event.setSeatsLeft(left);
        } else {
            event.setSeatsLeft(null); // Unlimited
        }

        // 2. Check if event has ended
        if (event.getEventDate() != null && event.getStartTime() != null) {
            ZonedDateTime eventStart = LocalDateTime.of(event.getEventDate(), event.getStartTime()).atZone(zoneId);
            if (now.isAfter(eventStart)) {
                event.setAvailability(AvailabilityStatus.ENDED);
                event.setAvailabilityLabel("Event Ended");
                return;
            }
        }

        // 3. Check if registration deadline has passed
        if (event.getRegistrationDeadline() != null) {
            ZonedDateTime deadline = event.getRegistrationDeadline().atZone(zoneId);
            if (now.isAfter(deadline)) {
                event.setAvailability(AvailabilityStatus.CLOSED);
                event.setAvailabilityLabel("Registration Closed");
                return;
            }
        }

        // 4. Check capacity constraint
        if (event.getCapacity() != null && event.getRegisteredCount() >= event.getCapacity()) {
            event.setAvailability(AvailabilityStatus.FULL);
            event.setAvailabilityLabel("Event Full");
            return;
        }

        // 5. Default to Open
        event.setAvailability(AvailabilityStatus.OPEN);
        event.setAvailabilityLabel("Open for Registration");
    }
}
