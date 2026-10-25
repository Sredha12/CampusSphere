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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for browsing and searching campus events.
 */
@WebServlet("/events")
public class EventListServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(EventListServlet.class.getName());

    private final EventService eventService = new EventService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("activeNav", "events");
        request.setAttribute("pageTitle", "Events - CampusSphere");

        try {
            List<String> categories = eventService.getCategories();

            String rawCategory = request.getParameter("category");
            String category = null;
            if (rawCategory != null && !rawCategory.isBlank()) {
                String trimmedCat = rawCategory.trim();
                if (categories.contains(trimmedCat)) {
                    category = trimmedCat;
                }
            }

            String rawQuery = request.getParameter("q");
            String query = null;
            if (rawQuery != null && !rawQuery.isBlank()) {
                query = rawQuery.trim();
                if (query.length() > 100) {
                    query = query.substring(0, 100);
                }
            }

            List<Event> events = eventService.searchEvents(category, query);

            request.setAttribute("events", events);
            request.setAttribute("categories", categories);
            request.setAttribute("selectedCategory", category);
            request.setAttribute("searchQuery", query);

            request.getRequestDispatcher("/WEB-INF/views/public/events.jsp").forward(request, response);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to load events catalog", e);
            throw new ServletException("Could not retrieve events catalog", e);
        }
    }
}
