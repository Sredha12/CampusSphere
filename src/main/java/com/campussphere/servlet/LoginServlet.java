package com.campussphere.servlet;

import com.campussphere.dao.DataAccessException;
import com.campussphere.model.Student;
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
 * Controller handling student authentication.
 */
@WebServlet(urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(LoginServlet.class.getName());

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
        request.setAttribute("pageTitle", "Student Login - CampusSphere");
        request.setAttribute("activeNav", "login");

        request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try {
            AuthService.AuthResult<Student> result = authService.authenticateStudent(
                    email, password, request.getRemoteAddr());

            if (result.isSuccess()) {
                Student student = result.getData();
                SessionUtil.loginStudent(request, student.getId(), student.getFullName());
                response.sendRedirect(request.getContextPath() + "/student/dashboard");
            } else {
                request.setAttribute("loginError", result.getGenericError());
                request.setAttribute("email", email != null ? email.trim() : "");
                request.setAttribute("pageTitle", "Student Login - CampusSphere");
                request.setAttribute("activeNav", "login");

                request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
            }
        } catch (DataAccessException e) {
            LOGGER.log(Level.SEVERE, "Unexpected data access error during student login", e);
            throw e;
        }
    }
}
