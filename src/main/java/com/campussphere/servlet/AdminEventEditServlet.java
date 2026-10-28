package com.campussphere.servlet;

import com.campussphere.dao.ClubDAO;
import com.campussphere.dao.DataAccessException;
import com.campussphere.model.Club;
import com.campussphere.model.Event;
import com.campussphere.service.ClubService;
import com.campussphere.service.EventService;
import com.campussphere.util.CsrfUtil;
import com.campussphere.util.FlashUtil;
import com.campussphere.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller to edit an existing campus event.
 */
@WebServlet(urlPatterns = {"/admin/events/edit"})
public class AdminEventEditServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(AdminEventEditServlet.class.getName());

    private static final List<String> CATEGORIES = List.of(
            "Technical", "Cultural", "Sports", "Workshop", "Seminar", "Other"
    );

    private final ClubDAO clubDAO = new ClubDAO();
    private final ClubService clubService = new ClubService(clubDAO);
    private final EventService eventService = new EventService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int id = parseEventId(request.getParameter("id"));
        if (id <= 0) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid event ID");
            return;
        }

        Event event = eventService.getEventById(id);
        if (event == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Event not found");
            return;
        }

        List<Club> clubs = clubService.getAllClubs();
        request.setAttribute("clubs", clubs);
        request.setAttribute("categories", CATEGORIES);
        request.setAttribute("eventId", event.getId());
        request.setAttribute("clubId", String.valueOf(event.getClubId()));
        request.setAttribute("title", event.getTitle());
        request.setAttribute("description", event.getDescription());
        request.setAttribute("category", event.getCategory());
        request.setAttribute("venue", event.getVenue());
        request.setAttribute("eventDate", event.getEventDate() != null ? event.getEventDate().toString() : "");
        request.setAttribute("startTime", event.getStartTime() != null ? event.getStartTime().toString() : "");
        request.setAttribute("endTime", event.getEndTime() != null ? event.getEndTime().toString() : "");
        request.setAttribute("registrationDeadline", event.getRegistrationDeadline() != null ? event.getRegistrationDeadline().toString() : "");
        request.setAttribute("capacity", event.getCapacity() != null ? String.valueOf(event.getCapacity()) : "");
        request.setAttribute("imageUrl", event.getImageUrl());
        request.setAttribute("registeredCount", event.getRegisteredCount());
        request.setAttribute("isEdit", true);
        request.setAttribute("pageTitle", "Edit Event: " + event.getTitle() + " - CampusSphere");
        request.setAttribute("adminActiveNav", "events");
        CsrfUtil.exposeToken(request);

        request.getRequestDispatcher("/WEB-INF/views/admin/events/form.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int id = parseEventId(request.getParameter("id"));
        if (id <= 0) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid event ID");
            return;
        }

        Event existing = eventService.getEventById(id);
        if (existing == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Event not found");
            return;
        }

        String clubIdStr = request.getParameter("clubId");
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String category = request.getParameter("category");
        String venue = request.getParameter("venue");
        String eventDateStr = request.getParameter("eventDate");
        String startTimeStr = request.getParameter("startTime");
        String endTimeStr = request.getParameter("endTime");
        String deadlineStr = request.getParameter("registrationDeadline");
        String capacityStr = request.getParameter("capacity");
        String imageUrl = request.getParameter("imageUrl");

        Integer clubId = null;
        try {
            if (clubIdStr != null && !clubIdStr.isBlank()) {
                clubId = Integer.parseInt(clubIdStr.trim());
            }
        } catch (NumberFormatException ignored) {
        }

        int currentRegistered = existing.getRegisteredCount();

        Map<String, String> errors = ValidationUtil.validateEvent(
                clubId, title, description, category, venue,
                eventDateStr, startTimeStr, endTimeStr, deadlineStr,
                capacityStr, imageUrl, currentRegistered, clubDAO);

        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
            request.setAttribute("clubs", clubService.getAllClubs());
            request.setAttribute("categories", CATEGORIES);
            request.setAttribute("eventId", id);
            request.setAttribute("clubId", clubIdStr);
            request.setAttribute("title", title);
            request.setAttribute("description", description);
            request.setAttribute("category", category);
            request.setAttribute("venue", venue);
            request.setAttribute("eventDate", eventDateStr);
            request.setAttribute("startTime", startTimeStr);
            request.setAttribute("endTime", endTimeStr);
            request.setAttribute("registrationDeadline", deadlineStr);
            request.setAttribute("capacity", capacityStr);
            request.setAttribute("imageUrl", imageUrl);
            request.setAttribute("registeredCount", currentRegistered);
            request.setAttribute("isEdit", true);
            request.setAttribute("pageTitle", "Edit Event: " + existing.getTitle() + " - CampusSphere");
            request.setAttribute("adminActiveNav", "events");
            CsrfUtil.exposeToken(request);

            request.getRequestDispatcher("/WEB-INF/views/admin/events/form.jsp").forward(request, response);
            return;
        }

        existing.setClubId(clubId);
        existing.setTitle(title.trim());
        existing.setDescription(description != null ? description.trim() : "");
        existing.setCategory(category.trim());
        existing.setVenue(venue.trim());
        existing.setEventDate(LocalDate.parse(eventDateStr.trim()));
        existing.setStartTime(LocalTime.parse(startTimeStr.trim()));
        existing.setEndTime(LocalTime.parse(endTimeStr.trim()));
        existing.setRegistrationDeadline(LocalDateTime.parse(deadlineStr.trim()));

        if (capacityStr != null && !capacityStr.isBlank()) {
            existing.setCapacity(Integer.parseInt(capacityStr.trim()));
        } else {
            existing.setCapacity(null);
        }

        existing.setImageUrl((imageUrl != null && !imageUrl.isBlank()) ? imageUrl.trim() : null);

        try {
            eventService.updateEvent(existing);
            FlashUtil.setSuccess(request, "Event \"" + existing.getTitle() + "\" updated successfully.");
            response.sendRedirect(request.getContextPath() + "/admin/events");
        } catch (DataAccessException e) {
            LOGGER.log(Level.SEVERE, "Failed to update event: " + id, e);
            errors.put("general", "A database error occurred while updating the event.");
            request.setAttribute("errors", errors);
            request.setAttribute("clubs", clubService.getAllClubs());
            request.setAttribute("categories", CATEGORIES);
            request.setAttribute("eventId", id);
            request.setAttribute("registeredCount", currentRegistered);
            request.setAttribute("isEdit", true);
            request.setAttribute("pageTitle", "Edit Event: " + existing.getTitle() + " - CampusSphere");
            request.setAttribute("adminActiveNav", "events");
            CsrfUtil.exposeToken(request);

            request.getRequestDispatcher("/WEB-INF/views/admin/events/form.jsp").forward(request, response);
        }
    }

    private int parseEventId(String idStr) {
        if (idStr == null || idStr.isBlank()) {
            return -1;
        }
        try {
            return Integer.parseInt(idStr.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
