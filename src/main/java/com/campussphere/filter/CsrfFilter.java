package com.campussphere.filter;

import com.campussphere.util.CsrfUtil;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Filter that protects against Cross-Site Request Forgery (CSRF).
 * <p>
 * Enforces that all POST requests carry a valid {@code _csrf} parameter matching the
 * session-stored CSRF token using constant-time comparison. Missing tokens or invalid
 * sessions result in an HTTP 403 Forbidden response.
 */
public class CsrfFilter implements Filter {

    private static final Logger LOGGER = Logger.getLogger(CsrfFilter.class.getName());

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

        String method = httpRequest.getMethod();

        if ("POST".equalsIgnoreCase(method)) {
            // Validate CSRF token for all POST requests
            if (!CsrfUtil.isValid(httpRequest)) {
                LOGGER.log(Level.WARNING, "Blocked POST request to {0} due to missing or invalid CSRF token",
                        httpRequest.getRequestURI());
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid or missing CSRF token");
                return;
            }
        } else {
            // On safe requests (GET, HEAD), expose the CSRF token if an active session exists
            HttpSession session = httpRequest.getSession(false);
            if (session != null) {
                String token = CsrfUtil.getOrCreateToken(session);
                httpRequest.setAttribute(CsrfUtil.REQ_ATTR, token);
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Cleanup if needed
    }
}
