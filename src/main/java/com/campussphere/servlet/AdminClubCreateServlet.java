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
 * Controller to create a new student club.
 */
@WebServlet(urlPatterns = {"/admin/clubs/new"})
public class AdminClubCreateServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(AdminClubCreateServlet.class.getName());

    private final ClubDAO clubDAO = new ClubDAO();
    private final ClubService clubService = new ClubService(clubDAO);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        CsrfUtil.exposeToken(request);
        request.setAttribute("isEdit", false);
        request.setAttribute("pageTitle", "Create New Club - CampusSphere Administration");
        request.setAttribute("adminActiveNav", "clubs-new");

        request.getRequestDispatcher("/WEB-INF/views/admin/clubs/form.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String name = request.getParameter("name");
        String description = request.getParameter("description");
        String contactEmail = request.getParameter("contactEmail");
        String imageUrl = request.getParameter("imageUrl");

        Map<String, String> errors = ValidationUtil.validateClub(
                name, description, contactEmail, imageUrl, null, clubDAO);

        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
            request.setAttribute("name", name);
            request.setAttribute("description", description);
            request.setAttribute("contactEmail", contactEmail);
            request.setAttribute("imageUrl", imageUrl);
            request.setAttribute("isEdit", false);
            request.setAttribute("pageTitle", "Create New Club - CampusSphere Administration");
            request.setAttribute("adminActiveNav", "clubs-new");
            CsrfUtil.exposeToken(request);

            request.getRequestDispatcher("/WEB-INF/views/admin/clubs/form.jsp").forward(request, response);
            return;
        }

        Club club = new Club();
        club.setName(name.trim());
        club.setDescription(description != null ? description.trim() : "");
        club.setContactEmail(contactEmail != null ? contactEmail.trim() : "");
        club.setImageUrl((imageUrl != null && !imageUrl.isBlank()) ? imageUrl.trim() : null);

        try {
            clubService.createClub(club);
            FlashUtil.setSuccess(request, "Club \"" + club.getName() + "\" created successfully.");
            response.sendRedirect(request.getContextPath() + "/admin/clubs");
        } catch (DataAccessException e) {
            LOGGER.log(Level.WARNING, "Failed to create club, possible duplicate: " + name, e);
            errors.put("name", "Could not create club. A club with this name may already exist.");
            request.setAttribute("errors", errors);
            request.setAttribute("name", name);
            request.setAttribute("description", description);
            request.setAttribute("contactEmail", contactEmail);
            request.setAttribute("imageUrl", imageUrl);
            request.setAttribute("isEdit", false);
            request.setAttribute("pageTitle", "Create New Club - CampusSphere Administration");
            request.setAttribute("adminActiveNav", "clubs-new");
            CsrfUtil.exposeToken(request);

            request.getRequestDispatcher("/WEB-INF/views/admin/clubs/form.jsp").forward(request, response);
        }
    }
}
