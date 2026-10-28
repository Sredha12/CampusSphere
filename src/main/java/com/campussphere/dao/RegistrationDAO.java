package com.campussphere.dao;

import com.campussphere.model.EventRegistration;
import com.campussphere.service.RegistrationResult;
import com.campussphere.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object managing persistence, retrieval, and transactional state
 * for student event registrations.
 */
public class RegistrationDAO {

    private static final Logger LOGGER = Logger.getLogger(RegistrationDAO.class.getName());

    /**
     * Checks if a student is currently actively registered for an event.
     */
    public boolean isStudentRegistered(int studentId, int eventId) {
        String sql = "SELECT COUNT(*) FROM event_registrations " +
                "WHERE student_id = ? AND event_id = ? AND status = 'REGISTERED'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, eventId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
                return false;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking registration status for studentId=" + studentId + " eventId=" + eventId, e);
            throw new DataAccessException("Failed to check registration status", e);
        }
    }

    /**
     * Retrieves an existing registration record for a student and event.
     */
    public EventRegistration findRegistration(int studentId, int eventId) {
        String sql = "SELECT r.id, r.student_id, r.event_id, r.registered_at, r.status, " +
                "       e.title AS event_title, e.club_id, c.name AS club_name, e.category, " +
                "       e.venue, e.event_date, e.start_time, e.end_time, e.registration_deadline, " +
                "       e.capacity, e.image_url " +
                "FROM event_registrations r " +
                "JOIN events e ON r.event_id = e.id " +
                "JOIN clubs c ON e.club_id = c.id " +
                "WHERE r.student_id = ? AND r.event_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, eventId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRegistration(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving registration for studentId=" + studentId + " eventId=" + eventId, e);
            throw new DataAccessException("Failed to load registration details", e);
        }
    }

    /**
     * Retrieves all event registrations for a specific student, sorted by event date descending.
     */
    public List<EventRegistration> findRegistrationsByStudentId(int studentId) {
        String sql = "SELECT r.id, r.student_id, r.event_id, r.registered_at, r.status, " +
                "       e.title AS event_title, e.club_id, c.name AS club_name, e.category, " +
                "       e.venue, e.event_date, e.start_time, e.end_time, e.registration_deadline, " +
                "       e.capacity, e.image_url " +
                "FROM event_registrations r " +
                "JOIN events e ON r.event_id = e.id " +
                "JOIN clubs c ON e.club_id = c.id " +
                "WHERE r.student_id = ? " +
                "ORDER BY e.event_date DESC, e.start_time DESC";

        List<EventRegistration> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRegistration(rs));
                }
                return list;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error querying registrations for studentId=" + studentId, e);
            throw new DataAccessException("Failed to load student registrations", e);
        }
    }

    /**
     * Executes the registration of a student for an event in a single ACID transaction.
     * <p>
     * Performs pessimistic row locking on the event row, validates availability and deadlines
     * against the application timezone, checks capacity, and inserts or reactivates the registration.
     */
    public RegistrationResult registerStudent(int studentId, int eventId, ZoneId zoneId) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Lock and inspect event row
            String eventSql = "SELECT id, title, event_date, start_time, end_time, registration_deadline, capacity " +
                    "FROM events WHERE id = ? FOR UPDATE";
            String eventTitle;
            LocalDate eventDate;
            LocalTime startTime;
            LocalTime endTime;
            LocalDateTime registrationDeadline;
            Integer capacity;

            try (PreparedStatement stmt = conn.prepareStatement(eventSql)) {
                stmt.setInt(1, eventId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return RegistrationResult.error("Event not found.");
                    }
                    eventTitle = rs.getString("title");
                    eventDate = rs.getObject("event_date", LocalDate.class);
                    startTime = rs.getObject("start_time", LocalTime.class);
                    endTime = rs.getObject("end_time", LocalTime.class);
                    registrationDeadline = rs.getObject("registration_deadline", LocalDateTime.class);

                    int cap = rs.getInt("capacity");
                    capacity = rs.wasNull() ? null : cap;
                }
            }

            // 2. Validate timing constraints in application timezone
            ZonedDateTime now = ZonedDateTime.now(zoneId);

            if (eventDate != null) {
                LocalTime finishTime = (endTime != null) ? endTime : startTime;
                if (finishTime != null) {
                    ZonedDateTime eventEnd = LocalDateTime.of(eventDate, finishTime).atZone(zoneId);
                    if (now.isAfter(eventEnd)) {
                        conn.rollback();
                        return RegistrationResult.error("Registration failed: this event has already ended.");
                    }
                }
            }

            if (registrationDeadline != null) {
                ZonedDateTime deadline = registrationDeadline.atZone(zoneId);
                if (now.isAfter(deadline)) {
                    conn.rollback();
                    return RegistrationResult.error("Registration failed: the registration deadline for this event has passed.");
                }
            }

            // 3. Check existing registration for this (student_id, event_id)
            String regCheckSql = "SELECT id, status FROM event_registrations WHERE student_id = ? AND event_id = ? FOR UPDATE";
            Integer existingRegId = null;
            String existingStatus = null;

            try (PreparedStatement stmt = conn.prepareStatement(regCheckSql)) {
                stmt.setInt(1, studentId);
                stmt.setInt(2, eventId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        existingRegId = rs.getInt("id");
                        existingStatus = rs.getString("status");
                    }
                }
            }

            if (EventRegistration.STATUS_REGISTERED.equalsIgnoreCase(existingStatus)) {
                conn.rollback();
                return RegistrationResult.error("You are already registered for this event.");
            }

            // 4. Check seat capacity constraint
            if (capacity != null) {
                String countSql = "SELECT COUNT(*) FROM event_registrations WHERE event_id = ? AND status = 'REGISTERED' FOR UPDATE";
                int currentRegistered;
                try (PreparedStatement stmt = conn.prepareStatement(countSql)) {
                    stmt.setInt(1, eventId);
                    try (ResultSet rs = stmt.executeQuery()) {
                        rs.next();
                        currentRegistered = rs.getInt(1);
                    }
                }

                if (currentRegistered >= capacity) {
                    conn.rollback();
                    return RegistrationResult.error("Registration failed: this event is currently full. No seats available.");
                }
            }

            // 5. Insert new registration or reactivate existing cancelled registration
            if (existingRegId != null) {
                String reactivateSql = "UPDATE event_registrations SET status = 'REGISTERED', registered_at = CURRENT_TIMESTAMP WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(reactivateSql)) {
                    stmt.setInt(1, existingRegId);
                    stmt.executeUpdate();
                }
            } else {
                String insertSql = "INSERT INTO event_registrations (student_id, event_id, status) VALUES (?, ?, 'REGISTERED')";
                try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                    stmt.setInt(1, studentId);
                    stmt.setInt(2, eventId);
                    stmt.executeUpdate();
                }
            }

            conn.commit();
            return RegistrationResult.success("Successfully registered for \"" + eventTitle + "\"!", eventTitle);

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    LOGGER.log(Level.SEVERE, "Rollback failed during registration", ex);
                }
            }
            LOGGER.log(Level.SEVERE, "Database error during event registration studentId=" + studentId + " eventId=" + eventId, e);
            throw new DataAccessException("Failed to complete event registration", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.WARNING, "Failed to restore connection state and close", e);
                }
            }
        }
    }

    /**
     * Executes cancellation of an existing event registration in a single transaction.
     */
    public RegistrationResult cancelRegistration(int studentId, int eventId, ZoneId zoneId) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Lock and check event timing
            String eventSql = "SELECT id, title, event_date, start_time FROM events WHERE id = ? FOR UPDATE";
            String eventTitle;
            LocalDate eventDate;
            LocalTime startTime;

            try (PreparedStatement stmt = conn.prepareStatement(eventSql)) {
                stmt.setInt(1, eventId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return RegistrationResult.error("Event not found.");
                    }
                    eventTitle = rs.getString("title");
                    eventDate = rs.getObject("event_date", LocalDate.class);
                    startTime = rs.getObject("start_time", LocalTime.class);
                }
            }

            ZonedDateTime now = ZonedDateTime.now(zoneId);
            if (eventDate != null && startTime != null) {
                ZonedDateTime eventStart = LocalDateTime.of(eventDate, startTime).atZone(zoneId);
                if (now.isAfter(eventStart)) {
                    conn.rollback();
                    return RegistrationResult.error("Cancellation failed: cannot cancel registration for an event that has already started or ended.");
                }
            }

            // 2. Lock and check existing registration
            String regSql = "SELECT id, status FROM event_registrations WHERE student_id = ? AND event_id = ? FOR UPDATE";
            Integer regId = null;
            String status = null;

            try (PreparedStatement stmt = conn.prepareStatement(regSql)) {
                stmt.setInt(1, studentId);
                stmt.setInt(2, eventId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        regId = rs.getInt("id");
                        status = rs.getString("status");
                    }
                }
            }

            if (regId == null || !EventRegistration.STATUS_REGISTERED.equalsIgnoreCase(status)) {
                conn.rollback();
                return RegistrationResult.error("You do not have an active registration for this event.");
            }

            // 3. Update status to CANCELLED
            String cancelSql = "UPDATE event_registrations SET status = 'CANCELLED' WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(cancelSql)) {
                stmt.setInt(1, regId);
                stmt.executeUpdate();
            }

            conn.commit();
            return RegistrationResult.success("Your registration for \"" + eventTitle + "\" has been cancelled.", eventTitle);

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    LOGGER.log(Level.SEVERE, "Rollback failed during cancellation", ex);
                }
            }
            LOGGER.log(Level.SEVERE, "Database error cancelling registration studentId=" + studentId + " eventId=" + eventId, e);
            throw new DataAccessException("Failed to cancel event registration", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.WARNING, "Failed to restore connection state and close", e);
                }
            }
        }
    }

    private EventRegistration mapRegistration(ResultSet rs) throws SQLException {
        EventRegistration r = new EventRegistration();
        r.setId(rs.getInt("id"));
        r.setStudentId(rs.getInt("student_id"));
        r.setEventId(rs.getInt("event_id"));
        r.setRegisteredAt(rs.getTimestamp("registered_at"));
        r.setStatus(rs.getString("status"));

        r.setEventTitle(rs.getString("event_title"));
        r.setClubId(rs.getInt("club_id"));
        r.setClubName(rs.getString("club_name"));
        r.setCategory(rs.getString("category"));
        r.setVenue(rs.getString("venue"));
        r.setEventDate(rs.getObject("event_date", LocalDate.class));
        r.setStartTime(rs.getObject("start_time", LocalTime.class));
        r.setEndTime(rs.getObject("end_time", LocalTime.class));
        r.setRegistrationDeadline(rs.getObject("registration_deadline", LocalDateTime.class));

        int cap = rs.getInt("capacity");
        r.setCapacity(rs.wasNull() ? null : cap);
        r.setImageUrl(rs.getString("image_url"));

        return r;
    }

    /**
     * Counts registrations by their status ('REGISTERED' or 'CANCELLED').
     *
     * @param status the registration status
     * @return count of matching registration records
     */
    public int countByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM event_registrations WHERE status = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting registrations by status=" + status, e);
            throw new DataAccessException("Failed to count registrations by status", e);
        }
    }

    /**
     * Retrieves the most recent registrations across the entire platform.
     *
     * @param limit maximum number of records to return
     * @return list of recent event registrations with student and event details
     */
    public List<EventRegistration> findRecentRegistrations(int limit) {
        String sql = "SELECT r.id, r.student_id, r.event_id, r.registered_at, r.status, " +
                "       s.full_name AS student_name, s.email AS student_email, " +
                "       e.title AS event_title, e.club_id, c.name AS club_name " +
                "FROM event_registrations r " +
                "JOIN students s ON r.student_id = s.id " +
                "JOIN events e ON r.event_id = e.id " +
                "JOIN clubs c ON e.club_id = c.id " +
                "ORDER BY r.registered_at DESC, r.id DESC " +
                "LIMIT ?";

        List<EventRegistration> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, Math.max(1, limit));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRegistrationWithStudent(rs));
                }
                return list;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error querying recent registrations", e);
            throw new DataAccessException("Failed to load recent registrations", e);
        }
    }

    /**
     * Retrieves registrations with optional event and status filters, ordered newest first.
     *
     * @param eventId optional event ID filter (must be > 0)
     * @param status  optional status filter ('REGISTERED' or 'CANCELLED')
     * @param limit   maximum number of rows to retrieve
     * @return list of filtered event registrations
     */
    public List<EventRegistration> findRegistrations(Integer eventId, String status, int limit) {
        StringBuilder sql = new StringBuilder(
                "SELECT r.id, r.student_id, r.event_id, r.registered_at, r.status, " +
                "       s.full_name AS student_name, s.email AS student_email, " +
                "       e.title AS event_title, e.club_id, c.name AS club_name " +
                "FROM event_registrations r " +
                "JOIN students s ON r.student_id = s.id " +
                "JOIN events e ON r.event_id = e.id " +
                "JOIN clubs c ON e.club_id = c.id "
        );

        List<Object> params = new ArrayList<>();
        boolean hasWhere = false;

        if (eventId != null && eventId > 0) {
            sql.append(hasWhere ? "AND " : "WHERE ");
            sql.append("r.event_id = ? ");
            params.add(eventId);
            hasWhere = true;
        }

        String validStatus = normalizeStatus(status);
        if (validStatus != null) {
            sql.append(hasWhere ? "AND " : "WHERE ");
            sql.append("r.status = ? ");
            params.add(validStatus);
            hasWhere = true;
        }

        sql.append("ORDER BY r.registered_at DESC, r.id DESC LIMIT ?");
        params.add(Math.max(1, limit));

        List<EventRegistration> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                Object p = params.get(i);
                if (p instanceof Integer intVal) {
                    stmt.setInt(i + 1, intVal);
                } else if (p instanceof String strVal) {
                    stmt.setString(i + 1, strVal);
                }
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRegistrationWithStudent(rs));
                }
                return list;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error querying filtered registrations", e);
            throw new DataAccessException("Failed to load filtered registrations", e);
        }
    }

    /**
     * Counts registrations matching optional event and status filters.
     *
     * @param eventId optional event ID filter
     * @param status  optional status filter
     * @return total matching count
     */
    public int countRegistrations(Integer eventId, String status) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM event_registrations r ");
        List<Object> params = new ArrayList<>();
        boolean hasWhere = false;

        if (eventId != null && eventId > 0) {
            sql.append(hasWhere ? "AND " : "WHERE ");
            sql.append("r.event_id = ? ");
            params.add(eventId);
            hasWhere = true;
        }

        String validStatus = normalizeStatus(status);
        if (validStatus != null) {
            sql.append(hasWhere ? "AND " : "WHERE ");
            sql.append("r.status = ? ");
            params.add(validStatus);
            hasWhere = true;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                Object p = params.get(i);
                if (p instanceof Integer intVal) {
                    stmt.setInt(i + 1, intVal);
                } else if (p instanceof String strVal) {
                    stmt.setString(i + 1, strVal);
                }
            }

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting filtered registrations", e);
            throw new DataAccessException("Failed to count filtered registrations", e);
        }
    }

    /**
     * Retrieves all active (status = 'REGISTERED') attendees for an event roster, ordered by registration time.
     *
     * @param eventId the event ID
     * @return list of active attendees with complete student information
     */
    public List<EventRegistration> findAttendeesByEventId(int eventId) {
        String sql = "SELECT r.id, r.student_id, r.event_id, r.registered_at, r.status, " +
                "       s.full_name AS student_name, s.email AS student_email, " +
                "       s.department AS student_department, s.year_of_study AS student_year_of_study, " +
                "       s.phone AS student_phone " +
                "FROM event_registrations r " +
                "JOIN students s ON r.student_id = s.id " +
                "WHERE r.event_id = ? AND r.status = 'REGISTERED' " +
                "ORDER BY r.registered_at ASC, r.id ASC";

        List<EventRegistration> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    EventRegistration reg = new EventRegistration();
                    reg.setId(rs.getInt("id"));
                    reg.setStudentId(rs.getInt("student_id"));
                    reg.setEventId(rs.getInt("event_id"));
                    reg.setRegisteredAt(rs.getTimestamp("registered_at"));
                    reg.setStatus(rs.getString("status"));
                    reg.setStudentName(rs.getString("student_name"));
                    reg.setStudentEmail(rs.getString("student_email"));
                    reg.setStudentDepartment(rs.getString("student_department"));
                    reg.setStudentYearOfStudy(rs.getInt("student_year_of_study"));
                    reg.setStudentPhone(rs.getString("student_phone"));
                    list.add(reg);
                }
                return list;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error querying attendees for eventId=" + eventId, e);
            throw new DataAccessException("Failed to load attendees for roster", e);
        }
    }

    private String normalizeStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        String trimmed = status.trim().toUpperCase();
        if (EventRegistration.STATUS_REGISTERED.equals(trimmed) || EventRegistration.STATUS_CANCELLED.equals(trimmed)) {
            return trimmed;
        }
        return null;
    }

    private EventRegistration mapRegistrationWithStudent(ResultSet rs) throws SQLException {
        EventRegistration r = new EventRegistration();
        r.setId(rs.getInt("id"));
        r.setStudentId(rs.getInt("student_id"));
        r.setEventId(rs.getInt("event_id"));
        r.setRegisteredAt(rs.getTimestamp("registered_at"));
        r.setStatus(rs.getString("status"));
        r.setStudentName(rs.getString("student_name"));
        r.setStudentEmail(rs.getString("student_email"));
        r.setEventTitle(rs.getString("event_title"));
        r.setClubId(rs.getInt("club_id"));
        r.setClubName(rs.getString("club_name"));
        return r;
    }
}
