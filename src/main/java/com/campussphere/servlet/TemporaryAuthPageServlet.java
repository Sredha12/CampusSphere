package com.campussphere.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Temporary informational placeholder for authentication routes.
 *
 * TODO: Replace with real AuthServlet in Phase 5.
 */
@WebServlet(urlPatterns = {"/login", "/register"})
public class TemporaryAuthPageServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();
        boolean isRegister = "/register".equalsIgnoreCase(path);

        request.setAttribute("authAction", isRegister ? "Student Registration" : "Student Login");
        request.setAttribute("activeNav", isRegister ? "register" : "login");
        request.setAttribute("pageTitle", (isRegister ? "Register" : "Login") + " - CampusSphere");

        request.getRequestDispatcher("/WEB-INF/views/public/coming-soon.jsp").forward(request, response);
    }
}
