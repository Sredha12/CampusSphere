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
 * Controller for displaying profile and upcoming events of a student club.
 */
@WebServlet("/clubs/detail")
public class ClubDetailServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(ClubDetailServlet.class.getName());

    private final ClubService clubService = new ClubService();
    private final EventService eventService = new EventService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isBlank()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Club ID is required");
            return;
        }

        int clubId;
        try {
            clubId = Integer.parseInt(idParam.trim());
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid club ID format");
            return;
        }

        try {
            Club club = clubService.getClubById(clubId);
            if (club == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Club not found");
                return;
            }

            List<Event> upcomingEvents = eventService.getEventsByClubId(clubId);

            request.setAttribute("club", club);
            request.setAttribute("upcomingEvents", upcomingEvents);
            request.setAttribute("activeNav", "clubs");
            request.setAttribute("pageTitle", club.getName() + " - CampusSphere");

            request.getRequestDispatcher("/WEB-INF/views/public/club-detail.jsp").forward(request, response);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to load club details for id=" + clubId, e);
            throw new ServletException("Could not retrieve club details", e);
        }
    }
}
