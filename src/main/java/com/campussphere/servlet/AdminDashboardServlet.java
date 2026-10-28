package com.campussphere.servlet;

import com.campussphere.dao.StudentDAO;
import com.campussphere.model.EventRegistration;
import com.campussphere.service.ClubService;
import com.campussphere.service.EventService;
import com.campussphere.service.RegistrationService;
import com.campussphere.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Controller displaying the authenticated administrator dashboard.
 * Protected by {@link com.campussphere.filter.AdminFilter}.
 */
@WebServlet(urlPatterns = {"/admin/dashboard"})
public class AdminDashboardServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final ClubService clubService = new ClubService();
    private final EventService eventService = new EventService();
    private final RegistrationService registrationService = new RegistrationService();
    private final StudentDAO studentDAO = new StudentDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String userName = SessionUtil.getUserName(request);

        int totalClubs = clubService.getTotalClubCount();
        int totalEvents = eventService.getTotalEventCount();
        int upcomingEvents = eventService.getUpcomingEventCount();
        int totalStudents = studentDAO.countAll();
        int activeRegistrations = registrationService.getActiveRegistrationCount();
        int cancelledRegistrations = registrationService.getCancelledRegistrationCount();
        List<EventRegistration> recentRegistrations = registrationService.getRecentRegistrations(5);

        request.setAttribute("userName", userName);
        request.setAttribute("totalClubs", totalClubs);
        request.setAttribute("totalEvents", totalEvents);
        request.setAttribute("upcomingEvents", upcomingEvents);
        request.setAttribute("totalStudents", totalStudents);
        request.setAttribute("activeRegistrations", activeRegistrations);
        request.setAttribute("cancelledRegistrations", cancelledRegistrations);
        request.setAttribute("recentRegistrations", recentRegistrations);

        request.setAttribute("pageTitle", "Administrator Dashboard - CampusSphere");
        request.setAttribute("activeNav", "adminDashboard");
        request.setAttribute("adminActiveNav", "dashboard");

        request.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(request, response);
    }
}
