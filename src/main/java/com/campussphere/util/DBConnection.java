package com.campussphere.util;

import com.campussphere.config.AppConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Database connection provider for CampusSphere.
 * <p>
 * Supplies direct JDBC connections using {@link DriverManager}. Callers must
 * manage connection lifecycles deterministically using try-with-resources blocks.
 */
public final class DBConnection {

    private static final Logger LOGGER = Logger.getLogger(DBConnection.class.getName());

    static {
        try {
            // Explicitly load MySQL JDBC Driver class
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "MySQL JDBC Driver class not found in runtime classpath", e);
        }
    }

    private DBConnection() {
        // Utility class; prevent instantiation
    }

    /**
     * Obtains a new JDBC Connection to the configured MySQL database.
     *
     * @return a live {@link Connection} instance
     * @throws SQLException if database is not configured or connection cannot be established
     */
    public static Connection getConnection() throws SQLException {
        AppConfig config = AppConfig.getInstance();

        if (!config.isDatabaseConfigured()) {
            throw new SQLException("Database is not configured. Please supply credentials in " +
                    "~/.campussphere/campussphere.properties or via CAMPUSSPHERE_DB_* environment variables.");
        }

        String host = config.getDbHost();
        String port = config.getDbPort();
        String name = config.getDbName();
        String user = config.getDbUser();
        String password = config.getDbPassword();

        // LOCAL DEVELOPMENT ONLY: allowPublicKeyRetrieval=true and useSSL=false are intended for local dev without TLS certificates.
        String jdbcUrl = "jdbc:mysql://" + host + ":" + port + "/" + name +
                "?useSSL=false&allowPublicKeyRetrieval=true&characterEncoding=UTF-8&connectTimeout=3000";

        // Log connection attempt safely without exposing credentials or full URL
        LOGGER.log(Level.FINE, "Attempting database connection to {0}:{1}/{2} as user {3}",
                new Object[]{host, port, name, user});

        try {
            return DriverManager.getConnection(jdbcUrl, user, password);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to connect to MySQL database at {0}:{1}/{2}",
                    new Object[]{host, port, name});
            throw e;
        }
    }
}
