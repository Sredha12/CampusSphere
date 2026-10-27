package com.campussphere.servlet;

import com.campussphere.util.FlashUtil;
import com.campussphere.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Controller handling user logout.
 * <p>
 * Strictly accepts POST requests protected with CSRF verification to prevent
 * unauthorized logout via image/link injection attacks.
 */
@WebServlet(urlPatterns = {"/logout"})
public class LogoutServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Logout must be requested via POST.");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        SessionUtil.logout(request);
        FlashUtil.setSuccess(request, "You have been logged out.");

        response.sendRedirect(request.getContextPath() + "/");
    }
}
