package com.campussphere.servlet;

import com.campussphere.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Controller displaying the authenticated administrator dashboard.
 * Protected by {@link com.campussphere.filter.AdminFilter}.
 */
@WebServlet(urlPatterns = {"/admin/dashboard"})
public class AdminDashboardServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String userName = SessionUtil.getUserName(request);

        request.setAttribute("userName", userName);
        request.setAttribute("pageTitle", "Administrator Dashboard - CampusSphere");
        request.setAttribute("activeNav", "adminDashboard");

        request.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(request, response);
    }
}
