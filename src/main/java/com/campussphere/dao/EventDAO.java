package com.campussphere.dao;

import com.campussphere.model.Event;
import com.campussphere.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object providing read-only queries for events.
 */
public class EventDAO {

    private static final Logger LOGGER = Logger.getLogger(EventDAO.class.getName());

    private static final String BASE_SELECT =
            "SELECT e.id, e.club_id, c.name AS club_name, e.title, e.description, e.category, " +
            "       e.venue, e.event_date, e.start_time, e.end_time, e.registration_deadline, " +
            "       e.capacity, e.image_url, COALESCE(r.reg_count, 0) AS registered_count " +
            "FROM events e " +
            "JOIN clubs c ON e.club_id = c.id " +
            "LEFT JOIN (" +
            "    SELECT event_id, COUNT(*) AS reg_count " +
            "    FROM event_registrations " +
            "    WHERE status = 'REGISTERED' " +
            "    GROUP BY event_id" +
            ") r ON e.id = r.event_id ";

    /**
     * Retrieves upcoming events ordered by event_date ASC, start_time ASC.
     */
    public List<Event> findUpcoming(int limit) {
        String sql = BASE_SELECT +
                "WHERE e.event_date >= CURDATE() " +
                "ORDER BY e.event_date ASC, e.start_time ASC " +
                "LIMIT ?";

        List<Event> events = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, Math.max(1, limit));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    events.add(mapEvent(rs));
                }
            }
            return events;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error querying upcoming events", e);
            throw new DataAccessException("Failed to load upcoming events", e);
        }
    }

    /**
     * Searches upcoming events by optional category and keyword.
     */
    public List<Event> search(String category, String keyword, LocalDate today) {
        StringBuilder sql = new StringBuilder(BASE_SELECT);
        sql.append("WHERE e.event_date >= ? ");

        boolean hasCategory = category != null && !category.isBlank();
        boolean hasKeyword = keyword != null && !keyword.isBlank();

        if (hasCategory) {
            sql.append("AND e.category = ? ");
        }

        if (hasKeyword) {
            sql.append("AND (e.title LIKE ? OR e.description LIKE ? OR e.venue LIKE ? OR c.name LIKE ?) ");
        }

        sql.append("ORDER BY e.event_date ASC, e.start_time ASC");

        List<Event> events = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            stmt.setDate(paramIndex++, Date.valueOf(today != null ? today : LocalDate.now()));

            if (hasCategory) {
                stmt.setString(paramIndex++, category.trim());
            }

            if (hasKeyword) {
                String pattern = "%" + escapeLike(keyword.trim()) + "%";
                stmt.setString(paramIndex++, pattern);
                stmt.setString(paramIndex++, pattern);
                stmt.setString(paramIndex++, pattern);
                stmt.setString(paramIndex++, pattern);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    events.add(mapEvent(rs));
                }
            }
            return events;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching events", e);
            throw new DataAccessException("Failed to search events", e);
        }
    }

    /**
     * Finds an event by its unique ID.
     */
    public Event findById(int id) {
        String sql = BASE_SELECT + "WHERE e.id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapEvent(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error querying event id=" + id, e);
            throw new DataAccessException("Failed to load event details", e);
        }
    }

    /**
     * Finds upcoming events for a specific club.
     */
    public List<Event> findByClubId(int clubId, LocalDate today) {
        String sql = BASE_SELECT +
                "WHERE e.club_id = ? AND e.event_date >= ? " +
                "ORDER BY e.event_date ASC, e.start_time ASC";

        List<Event> events = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, clubId);
            stmt.setDate(2, Date.valueOf(today != null ? today : LocalDate.now()));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    events.add(mapEvent(rs));
                }
            }
            return events;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error querying events for clubId=" + clubId, e);
            throw new DataAccessException("Failed to load club events", e);
        }
    }

    /**
     * Retrieves all distinct event categories.
     */
    public List<String> findCategories() {
        String sql = "SELECT DISTINCT category FROM events WHERE category IS NOT NULL ORDER BY category ASC";

        List<String> categories = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                categories.add(rs.getString("category"));
            }
            return categories;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error querying event categories", e);
            throw new DataAccessException("Failed to load event categories", e);
        }
    }

    private Event mapEvent(ResultSet rs) throws SQLException {
        Event event = new Event();
        event.setId(rs.getInt("id"));
        event.setClubId(rs.getInt("club_id"));
        event.setClubName(rs.getString("club_name"));
        event.setTitle(rs.getString("title"));
        event.setDescription(rs.getString("description"));
        event.setCategory(rs.getString("category"));
        event.setVenue(rs.getString("venue"));

        event.setEventDate(rs.getObject("event_date", LocalDate.class));
        event.setStartTime(rs.getObject("start_time", LocalTime.class));
        event.setEndTime(rs.getObject("end_time", LocalTime.class));
        event.setRegistrationDeadline(rs.getObject("registration_deadline", LocalDateTime.class));

        int cap = rs.getInt("capacity");
        event.setCapacity(rs.wasNull() ? null : cap);

        event.setImageUrl(rs.getString("image_url"));
        event.setRegisteredCount(rs.getInt("registered_count"));

        return event;
    }

    private String escapeLike(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\")
                    .replace("%", "\\%")
                    .replace("_", "\\_");
    }
}
