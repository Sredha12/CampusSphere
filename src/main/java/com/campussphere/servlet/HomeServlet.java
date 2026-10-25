package com.campussphere.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Serves the primary landing page for CampusSphere.
 */
@WebServlet(urlPatterns = {"", "/home"})
public class HomeServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Pass attributes to demonstrate and verify JSTL rendering
        request.setAttribute("foundationStatus", "Active");
        request.setAttribute("systemName", "CampusSphere");
        request.setAttribute("welcomeMessage", "Every Event. One Campus. One Platform.");

        request.getRequestDispatcher("/WEB-INF/views/public/home.jsp").forward(request, response);
    }
}
