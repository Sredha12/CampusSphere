package com.campussphere.servlet;

import com.campussphere.dao.DataAccessException;
import com.campussphere.model.Admin;
import com.campussphere.service.AuthService;
import com.campussphere.util.CsrfUtil;
import com.campussphere.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller handling administrator authentication.
 */
@WebServlet(urlPatterns = {"/admin/login"})
public class AdminLoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(AdminLoginServlet.class.getName());

    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // If already logged in as admin, redirect directly to dashboard
        if (SessionUtil.isAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            return;
        }

        CsrfUtil.exposeToken(request);
        request.setAttribute("pageTitle", "Administrator Login - CampusSphere");

        request.getRequestDispatcher("/WEB-INF/views/auth/admin-login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try {
            AuthService.AuthResult<Admin> result = authService.authenticateAdmin(
                    email, password, request.getRemoteAddr());

            if (result.isSuccess()) {
                Admin admin = result.getData();
                SessionUtil.loginAdmin(request, admin.getId(), admin.getFullName());
                response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            } else {
                request.setAttribute("loginError", result.getGenericError());
                request.setAttribute("email", email != null ? email.trim() : "");
                request.setAttribute("pageTitle", "Administrator Login - CampusSphere");

                request.getRequestDispatcher("/WEB-INF/views/auth/admin-login.jsp").forward(request, response);
            }
        } catch (DataAccessException e) {
            LOGGER.log(Level.SEVERE, "Unexpected data access error during admin login", e);
            throw e;
        }
    }
}
