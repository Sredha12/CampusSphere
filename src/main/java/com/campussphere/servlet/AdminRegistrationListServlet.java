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
 * Controller displaying the list of all student event registrations for administrators,
 * with optional filtering by event and registration status.
 * Protected by {@link com.campussphere.filter.AdminFilter}.
 */
@WebServlet(urlPatterns = {"/admin/registrations"})
public class AdminRegistrationListServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final RegistrationService registrationService = new RegistrationService();
    private final EventService eventService = new EventService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String eventIdParam = request.getParameter("eventId");
        Integer eventId = null;
        if (eventIdParam != null && !eventIdParam.isBlank()) {
            try {
                int parsed = Integer.parseInt(eventIdParam.trim());
                if (parsed > 0) {
                    eventId = parsed;
                }
            } catch (NumberFormatException ignored) {
                // Ignore invalid value safely
            }
        }

        String statusParam = request.getParameter("status");
        String status = null;
        if (statusParam != null && !statusParam.isBlank()) {
            String trimmed = statusParam.trim().toUpperCase();
            if (EventRegistration.STATUS_REGISTERED.equals(trimmed) || EventRegistration.STATUS_CANCELLED.equals(trimmed)) {
                status = trimmed;
            }
        }

        List<EventRegistration> registrations = registrationService.getRegistrations(eventId, status, 200);
        int totalCount = registrationService.countRegistrations(eventId, status);
        boolean hasMore = totalCount > 200;

        List<Event> events = eventService.getAllEventsForAdmin();

        request.setAttribute("registrations", registrations);
        request.setAttribute("totalCount", totalCount);
        request.setAttribute("hasMore", hasMore);
        request.setAttribute("selectedEventId", eventId);
        request.setAttribute("selectedStatus", status);
        request.setAttribute("events", events);
        request.setAttribute("pageTitle", "Event Registrations - CampusSphere Administration");
        request.setAttribute("adminActiveNav", "registrations");

        request.getRequestDispatcher("/WEB-INF/views/admin/registrations/list.jsp").forward(request, response);
    }
}
