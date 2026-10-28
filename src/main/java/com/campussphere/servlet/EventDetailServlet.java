package com.campussphere.servlet;

import com.campussphere.model.Event;
import com.campussphere.service.EventService;
import com.campussphere.service.RegistrationService;
import com.campussphere.util.CsrfUtil;
import com.campussphere.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for displaying full details of a single event.
 */
@WebServlet("/events/detail")
public class EventDetailServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(EventDetailServlet.class.getName());

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
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid event ID format");
            return;
        }

        try {
            Event event = eventService.getEventById(eventId);
            if (event == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Event not found");
                return;
            }

            boolean isRegistered = false;
            if (SessionUtil.isStudent(request)) {
                Integer studentId = SessionUtil.getUserId(request);
                if (studentId != null) {
                    isRegistered = registrationService.isStudentRegistered(studentId, eventId);
                }
            }

            request.setAttribute("event", event);
            request.setAttribute("isRegistered", isRegistered);
            request.setAttribute("activeNav", "events");
            request.setAttribute("pageTitle", event.getTitle() + " - CampusSphere");
            CsrfUtil.exposeToken(request);

            request.getRequestDispatcher("/WEB-INF/views/public/event-detail.jsp").forward(request, response);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to load event details for id=" + eventId, e);
            throw new ServletException("Could not retrieve event details", e);
        }
    }
}
