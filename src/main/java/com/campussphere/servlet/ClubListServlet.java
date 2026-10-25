package com.campussphere.servlet;

import com.campussphere.model.Club;
import com.campussphere.service.ClubService;
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
 * Controller for listing student clubs and societies.
 */
@WebServlet("/clubs")
public class ClubListServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(ClubListServlet.class.getName());

    private final ClubService clubService = new ClubService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("activeNav", "clubs");
        request.setAttribute("pageTitle", "Clubs & Societies - CampusSphere");

        try {
            List<Club> clubs = clubService.getAllClubs();
            request.setAttribute("clubs", clubs);
            request.getRequestDispatcher("/WEB-INF/views/public/clubs.jsp").forward(request, response);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to load clubs list", e);
            throw new ServletException("Could not retrieve clubs list", e);
        }
    }
}
