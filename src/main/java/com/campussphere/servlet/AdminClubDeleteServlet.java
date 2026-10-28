package com.campussphere.servlet;

import com.campussphere.dao.DataAccessException;
import com.campussphere.model.Club;
import com.campussphere.service.ClubService;
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
 * Controller to handle club deletion confirmation and execution.
 */
@WebServlet(urlPatterns = {"/admin/clubs/delete", "/admin/clubs/delete-confirm"})
public class AdminClubDeleteServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(AdminClubDeleteServlet.class.getName());

    private final ClubService clubService = new ClubService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int id = parseClubId(request.getParameter("id"));
        if (id <= 0) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid club ID");
            return;
        }

        Club club = clubService.getClubById(id);
        if (club == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Club not found");
            return;
        }

        int eventCount = clubService.getEventCountForClub(id);
        request.setAttribute("club", club);
        request.setAttribute("eventCount", eventCount);
        request.setAttribute("pageTitle", "Delete Club: " + club.getName() + " - CampusSphere");
        request.setAttribute("adminActiveNav", "clubs");
        CsrfUtil.exposeToken(request);

        request.getRequestDispatcher("/WEB-INF/views/admin/clubs/delete-confirm.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int id = parseClubId(request.getParameter("id"));
        if (id <= 0) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid club ID");
            return;
        }

        Club club = clubService.getClubById(id);
        if (club == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Club not found");
            return;
        }

        int eventCount = clubService.getEventCountForClub(id);
        if (eventCount > 0) {
            FlashUtil.setError(request, "This club has " + eventCount + " event(s). Delete or move those events first.");
            response.sendRedirect(request.getContextPath() + "/admin/clubs");
            return;
        }

        try {
            boolean deleted = clubService.deleteClub(id);
            if (deleted) {
                FlashUtil.setSuccess(request, "Club \"" + club.getName() + "\" was deleted successfully.");
            } else {
                FlashUtil.setError(request, "Could not delete club. It may have already been removed.");
            }
        } catch (IllegalStateException e) {
            FlashUtil.setError(request, e.getMessage());
        } catch (DataAccessException e) {
            LOGGER.log(Level.WARNING, "Database constraint error while deleting club: " + id, e);
            FlashUtil.setError(request, "Cannot delete club because events or records are still associated with it.");
        }

        response.sendRedirect(request.getContextPath() + "/admin/clubs");
    }

    private int parseClubId(String idStr) {
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
