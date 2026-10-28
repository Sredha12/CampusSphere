package com.campussphere.servlet;

import com.campussphere.model.Event;
import com.campussphere.model.EventRegistration;
import com.campussphere.service.EventService;
import com.campussphere.service.RegistrationService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Controller displaying the printable attendee roster for a specific event.
 * Displays only active registrations (status = 'REGISTERED').
 * Unknown or non-numeric id triggers a 404 response.
 * Protected by {@link com.campussphere.filter.AdminFilter}.
 */
@WebServlet(urlPatterns = {"/admin/events/roster"})
public class AdminEventRosterServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final EventService eventService = new EventService();
    private final RegistrationService registrationService = new RegistrationService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isBlank()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Event ID is required");
            return;
        }

        int eventId;
        try {
            eventId = Integer.parseInt(idParam.trim());
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid event ID");
            return;
        }

        if (eventId <= 0) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid event ID");
            return;
        }

        Event event = eventService.getEventById(eventId);
        if (event == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Event not found");
            return;
        }

        List<EventRegistration> attendees = registrationService.getEventAttendees(eventId);

        request.setAttribute("event", event);
        request.setAttribute("attendees", attendees);
        request.setAttribute("pageTitle", "Attendee Roster: " + event.getTitle() + " - CampusSphere Administration");
        request.setAttribute("adminActiveNav", "events");

        request.getRequestDispatcher("/WEB-INF/views/admin/events/roster.jsp").forward(request, response);
    }
}
