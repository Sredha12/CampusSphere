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
 * Controller to create a new campus event.
 */
@WebServlet(urlPatterns = {"/admin/events/new"})
public class AdminEventCreateServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(AdminEventCreateServlet.class.getName());

    private static final List<String> CATEGORIES = List.of(
            "Technical", "Cultural", "Sports", "Workshop", "Seminar", "Other"
    );

    private final ClubDAO clubDAO = new ClubDAO();
    private final ClubService clubService = new ClubService(clubDAO);
    private final EventService eventService = new EventService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Club> clubs = clubService.getAllClubs();
        request.setAttribute("clubs", clubs);
        request.setAttribute("categories", CATEGORIES);
        request.setAttribute("isEdit", false);
        request.setAttribute("pageTitle", "Create New Event - CampusSphere Administration");
        request.setAttribute("adminActiveNav", "events-new");
        CsrfUtil.exposeToken(request);

        request.getRequestDispatcher("/WEB-INF/views/admin/events/form.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

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

        Map<String, String> errors = ValidationUtil.validateEvent(
                clubId, title, description, category, venue,
                eventDateStr, startTimeStr, endTimeStr, deadlineStr,
                capacityStr, imageUrl, null, clubDAO);

        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
            request.setAttribute("clubs", clubService.getAllClubs());
            request.setAttribute("categories", CATEGORIES);
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
            request.setAttribute("isEdit", false);
            request.setAttribute("pageTitle", "Create New Event - CampusSphere Administration");
            request.setAttribute("adminActiveNav", "events-new");
            CsrfUtil.exposeToken(request);

            request.getRequestDispatcher("/WEB-INF/views/admin/events/form.jsp").forward(request, response);
            return;
        }

        Event event = new Event();
        event.setClubId(clubId);
        event.setTitle(title.trim());
        event.setDescription(description != null ? description.trim() : "");
        event.setCategory(category.trim());
        event.setVenue(venue.trim());
        event.setEventDate(LocalDate.parse(eventDateStr.trim()));
        event.setStartTime(LocalTime.parse(startTimeStr.trim()));
        event.setEndTime(LocalTime.parse(endTimeStr.trim()));
        event.setRegistrationDeadline(LocalDateTime.parse(deadlineStr.trim()));

        if (capacityStr != null && !capacityStr.isBlank()) {
            event.setCapacity(Integer.parseInt(capacityStr.trim()));
        } else {
            event.setCapacity(null);
        }

        event.setImageUrl((imageUrl != null && !imageUrl.isBlank()) ? imageUrl.trim() : null);

        try {
            eventService.createEvent(event);
            FlashUtil.setSuccess(request, "Event \"" + event.getTitle() + "\" created successfully.");
            response.sendRedirect(request.getContextPath() + "/admin/events");
        } catch (DataAccessException e) {
            LOGGER.log(Level.SEVERE, "Failed to create event: " + title, e);
            errors.put("general", "A database error occurred while creating the event.");
            request.setAttribute("errors", errors);
            request.setAttribute("clubs", clubService.getAllClubs());
            request.setAttribute("categories", CATEGORIES);
            request.setAttribute("isEdit", false);
            request.setAttribute("pageTitle", "Create New Event - CampusSphere Administration");
            request.setAttribute("adminActiveNav", "events-new");
            CsrfUtil.exposeToken(request);

            request.getRequestDispatcher("/WEB-INF/views/admin/events/form.jsp").forward(request, response);
        }
    }
}
