package com.campussphere.servlet;

import com.campussphere.dao.ClubDAO;
import com.campussphere.dao.DataAccessException;
import com.campussphere.model.Club;
import com.campussphere.service.ClubService;
import com.campussphere.util.CsrfUtil;
import com.campussphere.util.FlashUtil;
import com.campussphere.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller to edit an existing student club.
 */
@WebServlet(urlPatterns = {"/admin/clubs/edit"})
public class AdminClubEditServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(AdminClubEditServlet.class.getName());

    private final ClubDAO clubDAO = new ClubDAO();
    private final ClubService clubService = new ClubService(clubDAO);

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

        request.setAttribute("clubId", club.getId());
        request.setAttribute("name", club.getName());
        request.setAttribute("description", club.getDescription());
        request.setAttribute("contactEmail", club.getContactEmail());
        request.setAttribute("imageUrl", club.getImageUrl());
        request.setAttribute("isEdit", true);
        request.setAttribute("pageTitle", "Edit Club: " + club.getName() + " - CampusSphere");
        request.setAttribute("adminActiveNav", "clubs");
        CsrfUtil.exposeToken(request);

        request.getRequestDispatcher("/WEB-INF/views/admin/clubs/form.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int id = parseClubId(request.getParameter("id"));
        if (id <= 0) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid club ID");
            return;
        }

        Club existing = clubService.getClubById(id);
        if (existing == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Club not found");
            return;
        }

        String name = request.getParameter("name");
        String description = request.getParameter("description");
        String contactEmail = request.getParameter("contactEmail");
        String imageUrl = request.getParameter("imageUrl");

        Map<String, String> errors = ValidationUtil.validateClub(
                name, description, contactEmail, imageUrl, id, clubDAO);

        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
            request.setAttribute("clubId", id);
            request.setAttribute("name", name);
            request.setAttribute("description", description);
            request.setAttribute("contactEmail", contactEmail);
            request.setAttribute("imageUrl", imageUrl);
            request.setAttribute("isEdit", true);
            request.setAttribute("pageTitle", "Edit Club: " + existing.getName() + " - CampusSphere");
            request.setAttribute("adminActiveNav", "clubs");
            CsrfUtil.exposeToken(request);

            request.getRequestDispatcher("/WEB-INF/views/admin/clubs/form.jsp").forward(request, response);
            return;
        }

        existing.setName(name.trim());
        existing.setDescription(description != null ? description.trim() : "");
        existing.setContactEmail(contactEmail != null ? contactEmail.trim() : "");
        existing.setImageUrl((imageUrl != null && !imageUrl.isBlank()) ? imageUrl.trim() : null);

        try {
            clubService.updateClub(existing);
            FlashUtil.setSuccess(request, "Club \"" + existing.getName() + "\" updated successfully.");
            response.sendRedirect(request.getContextPath() + "/admin/clubs");
        } catch (DataAccessException e) {
            LOGGER.log(Level.WARNING, "Failed to update club: " + id, e);
            errors.put("name", "Could not update club. A club with this name may already exist.");
            request.setAttribute("errors", errors);
            request.setAttribute("clubId", id);
            request.setAttribute("name", name);
            request.setAttribute("description", description);
            request.setAttribute("contactEmail", contactEmail);
            request.setAttribute("imageUrl", imageUrl);
            request.setAttribute("isEdit", true);
            request.setAttribute("pageTitle", "Edit Club: " + existing.getName() + " - CampusSphere");
            request.setAttribute("adminActiveNav", "clubs");
            CsrfUtil.exposeToken(request);

            request.getRequestDispatcher("/WEB-INF/views/admin/clubs/form.jsp").forward(request, response);
        }
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
