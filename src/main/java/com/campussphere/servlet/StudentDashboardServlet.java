package com.campussphere.servlet;

import com.campussphere.model.EventRegistration;
import com.campussphere.service.RegistrationService;
import com.campussphere.util.CsrfUtil;
import com.campussphere.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Controller displaying the authenticated student dashboard.
 * Loads the logged-in student's active and historical event registrations.
 * Protected by {@link com.campussphere.filter.AuthFilter}.
 */
@WebServlet(urlPatterns = {"/student/dashboard"})
public class StudentDashboardServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final RegistrationService registrationService = new RegistrationService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Integer studentId = SessionUtil.getUserId(request);
        String userName = SessionUtil.getUserName(request);

        List<EventRegistration> registrations = Collections.emptyList();
        if (studentId != null) {
            registrations = registrationService.getStudentRegistrations(studentId);
        }

        request.setAttribute("userName", userName);
        request.setAttribute("registrations", registrations);
        request.setAttribute("pageTitle", "Student Dashboard - CampusSphere");
        request.setAttribute("activeNav", "dashboard");
        CsrfUtil.exposeToken(request);

        request.getRequestDispatcher("/WEB-INF/views/student/dashboard.jsp").forward(request, response);
    }
}
