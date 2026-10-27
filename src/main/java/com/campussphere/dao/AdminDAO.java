package com.campussphere.dao;

import com.campussphere.model.Admin;
import com.campussphere.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object managing persistence and retrieval of {@link Admin} records.
 */
public class AdminDAO {

    private static final Logger LOGGER = Logger.getLogger(AdminDAO.class.getName());

    /**
     * Finds an administrator by email address.
     * The email is automatically trimmed and converted to lowercase.
     *
     * @param email the email address
     * @return the matching Admin entity, or null if not found
     */
    public Admin findByEmail(String email) {
        if (email == null) {
            return null;
        }
        String normalizedEmail = email.trim().toLowerCase();

        String sql = "SELECT id, full_name, email, password_hash, created_at FROM admins WHERE email = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, normalizedEmail);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapAdmin(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error retrieving admin by email", e);
            throw new DataAccessException("Failed to query admin by email", e);
        }
    }

    /**
     * Counts the total number of admin records in the database.
     * Used by the setup bootstrap guard.
     *
     * @return count of admin rows
     */
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM admins";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error counting admins", e);
            throw new DataAccessException("Failed to count admin accounts", e);
        }
    }

    /**
     * Inserts the initial administrator in an atomic statement ONLY if no administrators
     * currently exist in the database.
     * <p>
     * Statement executed:
     * {@code INSERT INTO admins (full_name, email, password_hash) SELECT ?, ?, ? FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM admins);}
     *
     * @param fullName     full name of the initial administrator
     * @param email        email address
     * @param passwordHash PBKDF2 hashed password
     * @return true if 1 row was inserted (admin created), false if an admin already existed
     */
    public boolean insertIfNoAdminExists(String fullName, String email, String passwordHash) {
        String normalizedEmail = (email != null) ? email.trim().toLowerCase() : "";

        String sql = "INSERT INTO admins (full_name, email, password_hash) " +
                "SELECT ?, ?, ? FROM DUAL " +
                "WHERE NOT EXISTS (SELECT 1 FROM admins)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, fullName != null ? fullName.trim() : "");
            stmt.setString(2, normalizedEmail);
            stmt.setString(3, passwordHash);

            int rows = stmt.executeUpdate();
            return rows == 1;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error executing atomic admin setup insert", e);
            throw new DataAccessException("Failed to initialize administrator account", e);
        }
    }

    private Admin mapAdmin(ResultSet rs) throws SQLException {
        Admin a = new Admin();
        a.setId(rs.getInt("id"));
        a.setFullName(rs.getString("full_name"));
        a.setEmail(rs.getString("email"));
        a.setPasswordHash(rs.getString("password_hash"));
        a.setCreatedAt(rs.getTimestamp("created_at"));
        return a;
    }
}
