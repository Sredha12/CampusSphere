package com.campussphere.servlet;

import com.campussphere.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Controller displaying the authenticated student dashboard.
 * Protected by {@link com.campussphere.filter.AuthFilter}.
 */
@WebServlet(urlPatterns = {"/student/dashboard"})
public class StudentDashboardServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String userName = SessionUtil.getUserName(request);

        request.setAttribute("userName", userName);
        request.setAttribute("pageTitle", "Student Dashboard - CampusSphere");
        request.setAttribute("activeNav", "dashboard");

        request.getRequestDispatcher("/WEB-INF/views/student/dashboard.jsp").forward(request, response);
    }
}
