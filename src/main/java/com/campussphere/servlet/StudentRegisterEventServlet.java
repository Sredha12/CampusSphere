package com.campussphere.servlet;

import com.campussphere.service.RegistrationResult;
import com.campussphere.service.RegistrationService;
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
 * Controller processing student event registrations.
 * Protected by {@link com.campussphere.filter.AuthFilter} and {@link com.campussphere.filter.CsrfFilter}.
 */
@WebServlet("/student/events/register")
public class StudentRegisterEventServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(StudentRegisterEventServlet.class.getName());

    private final RegistrationService registrationService = new RegistrationService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Direct GET is not supported for state changes; redirect safely to events catalog
        response.sendRedirect(request.getContextPath() + "/events");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Integer studentId = SessionUtil.getUserId(request);
        if (studentId == null || !SessionUtil.isStudent(request)) {
            FlashUtil.setError(request, "Please log in with a student account to register for events.");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String eventIdParam = request.getParameter("eventId");
        if (eventIdParam == null || eventIdParam.isBlank()) {
            FlashUtil.setError(request, "Invalid event specified for registration.");
            response.sendRedirect(request.getContextPath() + "/events");
            return;
        }

        int eventId;
        try {
            eventId = Integer.parseInt(eventIdParam.trim());
        } catch (NumberFormatException e) {
            FlashUtil.setError(request, "Invalid event ID format.");
            response.sendRedirect(request.getContextPath() + "/events");
            return;
        }

        try {
            RegistrationResult result = registrationService.registerStudent(studentId, eventId);
            if (result.isSuccess()) {
                FlashUtil.setSuccess(request, result.getMessage());
            } else {
                FlashUtil.setError(request, result.getMessage());
            }
            response.sendRedirect(request.getContextPath() + "/events/detail?id=" + eventId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error processing registration for studentId=" + studentId + " eventId=" + eventId, e);
            FlashUtil.setError(request, "A system error occurred while processing your registration. Please try again later.");
            response.sendRedirect(request.getContextPath() + "/events/detail?id=" + eventId);
        }
    }
}
