package com.campussphere.service;

import com.campussphere.config.AppConfig;
import com.campussphere.dao.EventDAO;
import com.campussphere.model.AvailabilityStatus;
import com.campussphere.model.Event;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
        if (event.getEventDate() != null) {
            LocalTime finishTime = event.getEndTime() != null ? event.getEndTime() : event.getStartTime();
            if (finishTime != null) {
                ZonedDateTime eventEnd = LocalDateTime.of(event.getEventDate(), finishTime).atZone(zoneId);
                if (now.isAfter(eventEnd)) {
                    event.setAvailability(AvailabilityStatus.ENDED);
                    event.setAvailabilityLabel("Event Ended");
                    return;
                }
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

    /**
     * Retrieves all events sorted by date descending for admin management,
     * enriched with availability status.
     */
    public List<Event> getAllEventsForAdmin() {
        List<Event> events = eventDAO.findAllSortedByDateDesc();
        events.forEach(this::enrichEventStatus);
        return events;
    }

    /**
     * Returns total events count in the database.
     */
    public int getTotalEventCount() {
        return eventDAO.countAll();
    }

    /**
     * Returns count of events starting after now in the application timezone.
     */
    public int getUpcomingEventCount() {
        return eventDAO.countUpcoming(AppConfig.getInstance().getZoneId());
    }

    /**
     * Returns total registrations count for an event.
     */
    public int getRegistrationCountForEvent(int eventId) {
        return eventDAO.countRegistrationsByEventId(eventId);
    }

    /**
     * Creates a new event.
     */
    public int createEvent(Event event) {
        return eventDAO.create(event);
    }

    /**
     * Updates an existing event.
     */
    public boolean updateEvent(Event event) {
        return eventDAO.update(event);
    }

    /**
     * Deletes an event. Registrations are removed by DB cascade.
     */
    public boolean deleteEvent(int eventId) {
        return eventDAO.delete(eventId);
    }
}
