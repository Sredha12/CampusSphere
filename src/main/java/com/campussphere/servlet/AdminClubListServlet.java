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

/**
 * Controller to display the administrative clubs management list.
 */
@WebServlet(urlPatterns = {"/admin/clubs"})
public class AdminClubListServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final ClubService clubService = new ClubService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Club> clubs = clubService.getAllClubsForAdmin();
        request.setAttribute("clubs", clubs);
        request.setAttribute("pageTitle", "Manage Clubs - CampusSphere Administration");
        request.setAttribute("adminActiveNav", "clubs");

        request.getRequestDispatcher("/WEB-INF/views/admin/clubs/list.jsp").forward(request, response);
    }
}
