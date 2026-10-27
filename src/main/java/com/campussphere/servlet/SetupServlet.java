package com.campussphere.servlet;

import com.campussphere.config.AppConfig;
import com.campussphere.dao.AdminDAO;
import com.campussphere.dao.DataAccessException;
import com.campussphere.service.AuthService;
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
 * One-time setup controller to bootstrap the initial system administrator.
 * <p>
 * Security constraints:
 * <ul>
 *   <li>Only accessible when no admin records exist in the database ({@code countAll() == 0}).</li>
 *   <li>Only accessible when a valid setup key is configured in application properties.</li>
 *   <li>Returns HTTP 404 once an administrator exists or if setup key is unconfigured.</li>
 *   <li>Setup key is verified in constant time and never echoed back or logged.</li>
 *   <li>Not linked anywhere in the user interface.</li>
 * </ul>
 */
@WebServlet(urlPatterns = {"/setup"})
public class SetupServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(SetupServlet.class.getName());

    private final AdminDAO adminDAO = new AdminDAO();
    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            if (!isSetupPermitted()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            CsrfUtil.exposeToken(request);
            request.setAttribute("pageTitle", "Initial Administrator Setup - CampusSphere");

            request.getRequestDispatcher("/WEB-INF/views/auth/setup.jsp").forward(request, response);
        } catch (DataAccessException e) {
            LOGGER.log(Level.SEVERE, "Database error during setup availability check", e);
            throw e;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            if (!isSetupPermitted()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            String setupKey = request.getParameter("setupKey");
            String fullName = request.getParameter("fullName");
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            String confirmPassword = request.getParameter("confirmPassword");

            AuthService.AuthResult<Void> result = authService.createFirstAdmin(
                    setupKey, fullName, email, password, confirmPassword, request.getRemoteAddr());

            if (result.isSuccess()) {
                FlashUtil.setSuccess(request, "Administrator created. Please log in.");
                response.sendRedirect(request.getContextPath() + "/admin/login");
            } else {
                request.setAttribute("errors", result.getFieldErrors());
                request.setAttribute("genericError", result.getGenericError());
                // Never echo the setup key or passwords back
                request.setAttribute("fullName", fullName);
                request.setAttribute("email", email);

                request.setAttribute("pageTitle", "Initial Administrator Setup - CampusSphere");

                request.getRequestDispatcher("/WEB-INF/views/auth/setup.jsp").forward(request, response);
            }
        } catch (DataAccessException e) {
            LOGGER.log(Level.SEVERE, "Database error creating administrator", e);
            throw e;
        }
    }

    private boolean isSetupPermitted() {
        AppConfig config = AppConfig.getInstance();
        if (!config.isSetupKeyConfigured()) {
            LOGGER.log(Level.WARNING, "Admin setup unavailable: setup key is not configured in properties or environment.");
            return false;
        }

        int adminCount = adminDAO.countAll();
        return adminCount == 0;
    }
}
