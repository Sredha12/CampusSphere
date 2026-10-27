package com.campussphere.servlet;

import com.campussphere.dao.DataAccessException;
import com.campussphere.service.AuthService;
import com.campussphere.util.CsrfUtil;
import com.campussphere.util.FlashUtil;
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
 * Controller handling self-service student registration.
 */
@WebServlet(urlPatterns = {"/register"})
public class RegisterServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(RegisterServlet.class.getName());

    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // If user is already logged in, redirect to appropriate dashboard
        if (SessionUtil.isLoggedIn(request)) {
            if (SessionUtil.isAdmin(request)) {
                response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            } else {
                response.sendRedirect(request.getContextPath() + "/student/dashboard");
            }
            return;
        }

        CsrfUtil.exposeToken(request);
        request.setAttribute("pageTitle", "Student Registration - CampusSphere");
        request.setAttribute("activeNav", "register");

        request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String department = request.getParameter("department");
        String yearOfStudy = request.getParameter("yearOfStudy");
        String phone = request.getParameter("phone");

        try {
            AuthService.AuthResult<Integer> result = authService.registerStudent(
                    fullName, email, password, confirmPassword, department, yearOfStudy, phone);

            if (result.isSuccess()) {
                FlashUtil.setSuccess(request, "Account created. Please log in.");
                response.sendRedirect(request.getContextPath() + "/login");
            } else {
                request.setAttribute("errors", result.getFieldErrors());
                // Provide sticky values (passwords are intentionally excluded)
                request.setAttribute("fullName", fullName);
                request.setAttribute("email", email);
                request.setAttribute("department", department);
                request.setAttribute("yearOfStudy", yearOfStudy);
                request.setAttribute("phone", phone);

                request.setAttribute("pageTitle", "Student Registration - CampusSphere");
                request.setAttribute("activeNav", "register");

                request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
            }
        } catch (DataAccessException e) {
            LOGGER.log(Level.SEVERE, "Unexpected data access error during student registration", e);
            throw e;
        }
    }
}
