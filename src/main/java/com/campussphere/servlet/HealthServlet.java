package com.campussphere.servlet;

import com.campussphere.config.AppConfig;
import com.campussphere.util.DBConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Diagnostic health endpoint verifying container and database operational status.
 *
 * TODO: restrict or remove /health before final delivery.
 */
@WebServlet("/health")
public class HealthServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(HealthServlet.class.getName());

    public static final String STATUS_CONFIGURED_OK = "CONFIGURED_OK";
    public static final String STATUS_NOT_CONFIGURED = "NOT_CONFIGURED";
    public static final String STATUS_UNREACHABLE = "UNREACHABLE";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String dbStatus;
        AppConfig config = AppConfig.getInstance();

        if (!config.isDatabaseConfigured()) {
            dbStatus = STATUS_NOT_CONFIGURED;
        } else {
            try (Connection conn = DBConnection.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT 1")) {

                if (rs.next()) {
                    dbStatus = STATUS_CONFIGURED_OK;
                } else {
                    dbStatus = STATUS_UNREACHABLE;
                }
            } catch (Exception e) {
                // Log the real exception on the server side only; never expose details to callers
                LOGGER.log(Level.WARNING, "Database health check query failed", e);
                dbStatus = STATUS_UNREACHABLE;
            }
        }

        request.setAttribute("dbStatus", dbStatus);
        request.getRequestDispatcher("/WEB-INF/views/public/health.jsp").forward(request, response);
    }
}
