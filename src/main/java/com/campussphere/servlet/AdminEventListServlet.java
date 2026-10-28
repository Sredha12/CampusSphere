package com.campussphere.servlet;

import com.campussphere.model.Event;
import com.campussphere.service.EventService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Controller to list all events for administrative management.
 */
@WebServlet(urlPatterns = {"/admin/events"})
public class AdminEventListServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final EventService eventService = new EventService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Event> events = eventService.getAllEventsForAdmin();
        request.setAttribute("events", events);
        request.setAttribute("pageTitle", "Manage Events - CampusSphere Administration");
        request.setAttribute("adminActiveNav", "events");

        request.getRequestDispatcher("/WEB-INF/views/admin/events/list.jsp").forward(request, response);
    }
}
