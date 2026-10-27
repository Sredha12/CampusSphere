package com.campussphere.filter;

import com.campussphere.util.FlashUtil;
import com.campussphere.util.SessionUtil;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Access control filter safeguarding {@code /admin/*} endpoints.
 * <p>
 * Allows public access to {@code /admin/login}. All other admin routes strictly
 * require an active session with the {@code ADMIN} role.
 * If a logged-in student attempts to access admin routes, they are redirected to {@code /login}.
 * Unauthenticated visitors are redirected to {@code /admin/login}.
 * All admin responses include {@code Cache-Control: no-store}.
 */
public class AdminFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // No special configuration needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (!(request instanceof HttpServletRequest httpRequest) || !(response instanceof HttpServletResponse httpResponse)) {
            chain.doFilter(request, response);
            return;
        }

        // Prevent browser caching of admin management responses
        httpResponse.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        httpResponse.setHeader("Pragma", "no-cache");

        String path = httpRequest.getServletPath();
        if ("/admin/login".equalsIgnoreCase(path)) {
            // Allow access to admin login page
            chain.doFilter(request, response);
            return;
        }

        // Check for active ADMIN role
        if (SessionUtil.isAdmin(httpRequest)) {
            chain.doFilter(request, response);
            return;
        }

        // Logged-in students must never access admin pages
        if (SessionUtil.isStudent(httpRequest)) {
            FlashUtil.setError(httpRequest, "Access denied. Administrator privileges required.");
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            return;
        }

        // Unauthenticated visitor
        httpResponse.sendRedirect(httpRequest.getContextPath() + "/admin/login");
    }

    @Override
    public void destroy() {
        // Cleanup if needed
    }
}
