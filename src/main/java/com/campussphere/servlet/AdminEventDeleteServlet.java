package com.campussphere.servlet;

import com.campussphere.dao.DataAccessException;
import com.campussphere.model.Event;
import com.campussphere.service.EventService;
import com.campussphere.util.CsrfUtil;
import com.campussphere.util.FlashUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller to handle event deletion confirmation and execution.
 */
@WebServlet(urlPatterns = {"/admin/events/delete", "/admin/events/delete-confirm"})
public class AdminEventDeleteServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(AdminEventDeleteServlet.class.getName());

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

        int registrationCount = eventService.getRegistrationCountForEvent(id);
        request.setAttribute("event", event);
        request.setAttribute("registrationCount", registrationCount);
        request.setAttribute("pageTitle", "Delete Event: " + event.getTitle() + " - CampusSphere");
        request.setAttribute("adminActiveNav", "events");
        CsrfUtil.exposeToken(request);

        request.getRequestDispatcher("/WEB-INF/views/admin/events/delete-confirm.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
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

        try {
            boolean deleted = eventService.deleteEvent(id);
            if (deleted) {
                FlashUtil.setSuccess(request, "Event \"" + event.getTitle() + "\" and its associated registrations were deleted successfully.");
            } else {
                FlashUtil.setError(request, "Could not delete event. It may have already been removed.");
            }
        } catch (DataAccessException e) {
            LOGGER.log(Level.SEVERE, "Database error deleting event: " + id, e);
            FlashUtil.setError(request, "Failed to delete event due to a database error.");
        }

        response.sendRedirect(request.getContextPath() + "/admin/events");
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
