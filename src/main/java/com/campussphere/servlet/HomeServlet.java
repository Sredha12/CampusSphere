package com.campussphere.servlet;

import com.campussphere.model.Club;
import com.campussphere.model.Event;
import com.campussphere.service.ClubService;
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
 * Serves the primary landing page for CampusSphere showcasing upcoming events and featured clubs.
 */
@WebServlet(urlPatterns = {"", "/home"})
public class HomeServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(HomeServlet.class.getName());

    private final EventService eventService = new EventService();
    private final ClubService clubService = new ClubService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("activeNav", "home");
        request.setAttribute("pageTitle", "Home - CampusSphere");

        try {
            List<Event> upcomingEvents = eventService.getUpcomingEvents(6);
            List<Club> featuredClubs = clubService.getFeaturedClubs(3);

            request.setAttribute("upcomingEvents", upcomingEvents);
            request.setAttribute("featuredClubs", featuredClubs);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to load homepage data", e);
            throw new ServletException("Could not retrieve homepage events and clubs", e);
        }

        request.getRequestDispatcher("/WEB-INF/views/public/home.jsp").forward(request, response);
    }
}
