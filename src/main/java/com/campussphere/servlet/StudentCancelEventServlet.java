package com.campussphere.servlet;

import com.campussphere.model.EventRegistration;
import com.campussphere.service.RegistrationResult;
import com.campussphere.service.RegistrationService;
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
 * Controller handling event cancellation confirmation and execution.
 * Protected by {@link com.campussphere.filter.AuthFilter} and {@link com.campussphere.filter.CsrfFilter}.
 */
@WebServlet(urlPatterns = {"/student/events/cancel", "/student/events/cancel-confirm"})
public class StudentCancelEventServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(StudentCancelEventServlet.class.getName());

    private final RegistrationService registrationService = new RegistrationService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();
        if ("/student/events/cancel-confirm".equals(path)) {
            handleCancelConfirmGet(request, response);
        } else {
            // Direct GET to /student/events/cancel is disallowed
            response.sendRedirect(request.getContextPath() + "/student/dashboard");
        }
    }

    private void handleCancelConfirmGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Integer studentId = SessionUtil.getUserId(request);
        if (studentId == null || !SessionUtil.isStudent(request)) {
            FlashUtil.setError(request, "Please log in to continue.");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isBlank()) {
            FlashUtil.setError(request, "No event specified for cancellation.");
            response.sendRedirect(request.getContextPath() + "/student/dashboard");
            return;
        }

        int eventId;
        try {
            eventId = Integer.parseInt(idParam.trim());
        } catch (NumberFormatException e) {
            FlashUtil.setError(request, "Invalid event ID format.");
            response.sendRedirect(request.getContextPath() + "/student/dashboard");
            return;
        }

        try {
            EventRegistration reg = registrationService.getRegistration(studentId, eventId);
            if (reg == null || !reg.isRegistered()) {
                FlashUtil.setError(request, "No active registration found for this event.");
                response.sendRedirect(request.getContextPath() + "/student/dashboard");
                return;
            }

            if (!reg.isCancellable()) {
                FlashUtil.setError(request, "Cannot cancel registration: this event has already started or ended.");
                response.sendRedirect(request.getContextPath() + "/events/detail?id=" + eventId);
                return;
            }

            request.setAttribute("registration", reg);
            request.setAttribute("pageTitle", "Confirm Cancellation - CampusSphere");
            request.setAttribute("activeNav", "dashboard");
            CsrfUtil.exposeToken(request);

            request.getRequestDispatcher("/WEB-INF/views/student/cancel-confirm.jsp").forward(request, response);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading cancellation confirmation for studentId=" + studentId + " eventId=" + eventId, e);
            FlashUtil.setError(request, "Failed to load registration details. Please try again.");
            response.sendRedirect(request.getContextPath() + "/student/dashboard");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Integer studentId = SessionUtil.getUserId(request);
        if (studentId == null || !SessionUtil.isStudent(request)) {
            FlashUtil.setError(request, "Please log in to continue.");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String eventIdParam = request.getParameter("eventId");
        if (eventIdParam == null || eventIdParam.isBlank()) {
            FlashUtil.setError(request, "No event specified for cancellation.");
            response.sendRedirect(request.getContextPath() + "/student/dashboard");
            return;
        }

        int eventId;
        try {
            eventId = Integer.parseInt(eventIdParam.trim());
        } catch (NumberFormatException e) {
            FlashUtil.setError(request, "Invalid event ID format.");
            response.sendRedirect(request.getContextPath() + "/student/dashboard");
            return;
        }

        String redirectTarget = request.getParameter("redirect");

        try {
            RegistrationResult result = registrationService.cancelRegistration(studentId, eventId);
            if (result.isSuccess()) {
                FlashUtil.setSuccess(request, result.getMessage());
            } else {
                FlashUtil.setError(request, result.getMessage());
            }

            if ("detail".equalsIgnoreCase(redirectTarget)) {
                response.sendRedirect(request.getContextPath() + "/events/detail?id=" + eventId);
            } else {
                response.sendRedirect(request.getContextPath() + "/student/dashboard");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error cancelling registration for studentId=" + studentId + " eventId=" + eventId, e);
            FlashUtil.setError(request, "A system error occurred while cancelling your registration.");
            response.sendRedirect(request.getContextPath() + "/student/dashboard");
        }
    }
}
