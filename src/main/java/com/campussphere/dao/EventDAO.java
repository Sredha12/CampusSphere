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
import java.time.ZoneId;
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

    /**
     * Retrieves all events sorted by event_date DESC, start_time DESC for administrative management.
     */
    public List<Event> findAllSortedByDateDesc() {
        String sql = BASE_SELECT + "ORDER BY e.event_date DESC, e.start_time DESC";

        List<Event> events = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                events.add(mapEvent(rs));
            }
            return events;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error querying all events for admin", e);
            throw new DataAccessException("Failed to load events for administration", e);
        }
    }

    /**
     * Counts all registrations for an event.
     */
    public int countRegistrationsByEventId(int eventId) {
        String sql = "SELECT COUNT(*) FROM event_registrations WHERE event_id = ? AND status = 'REGISTERED'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting registrations for eventId=" + eventId, e);
            throw new DataAccessException("Failed to count registrations for event", e);
        }
    }

    /**
     * Counts the total number of events in the database.
     *
     * @return total events count
     */
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM events";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting total events", e);
            throw new DataAccessException("Failed to count events", e);
        }
    }

    /**
     * Counts events starting strictly after the current moment in the specified application timezone.
     * An event is upcoming if its event_date is after today, or if event_date is today and start_time is after now.
     *
     * @param zoneId application timezone
     * @return upcoming events count
     */
    public int countUpcoming(ZoneId zoneId) {
        java.time.ZonedDateTime now = java.time.ZonedDateTime.now(zoneId != null ? zoneId : java.time.ZoneId.of("Asia/Kolkata"));
        LocalDate today = now.toLocalDate();
        LocalTime currentTime = now.toLocalTime();

        String sql = "SELECT COUNT(*) FROM events WHERE event_date > ? OR (event_date = ? AND start_time > ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(today));
            stmt.setDate(2, Date.valueOf(today));
            stmt.setTime(3, java.sql.Time.valueOf(currentTime));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting upcoming events", e);
            throw new DataAccessException("Failed to count upcoming events", e);
        }
    }

    /**
     * Inserts a new event.
     *
     * @param event the event to insert
     * @return generated ID
     */
    public int create(Event event) {
        String sql = "INSERT INTO events (club_id, title, description, category, venue, " +
                "event_date, start_time, end_time, registration_deadline, capacity, image_url) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, event.getClubId());
            stmt.setString(2, event.getTitle().trim());
            stmt.setString(3, event.getDescription().trim());
            stmt.setString(4, event.getCategory().trim());
            stmt.setString(5, event.getVenue().trim());
            stmt.setDate(6, Date.valueOf(event.getEventDate()));
            stmt.setTime(7, java.sql.Time.valueOf(event.getStartTime()));
            stmt.setTime(8, java.sql.Time.valueOf(event.getEndTime()));
            stmt.setTimestamp(9, java.sql.Timestamp.valueOf(event.getRegistrationDeadline()));

            if (event.getCapacity() != null) {
                stmt.setInt(10, event.getCapacity());
            } else {
                stmt.setNull(10, java.sql.Types.INTEGER);
            }

            if (event.getImageUrl() != null && !event.getImageUrl().isBlank()) {
                stmt.setString(11, event.getImageUrl().trim());
            } else {
                stmt.setNull(11, java.sql.Types.VARCHAR);
            }

            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    event.setId(id);
                    return id;
                }
            }
            throw new DataAccessException("Failed to retrieve generated ID for event");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error inserting event", e);
            throw new DataAccessException("Failed to create event", e);
        }
    }

    /**
     * Updates an existing event.
     *
     * @param event the updated event entity
     * @return true if updated
     */
    public boolean update(Event event) {
        String sql = "UPDATE events SET club_id = ?, title = ?, description = ?, category = ?, venue = ?, " +
                "event_date = ?, start_time = ?, end_time = ?, registration_deadline = ?, capacity = ?, image_url = ? " +
                "WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, event.getClubId());
            stmt.setString(2, event.getTitle().trim());
            stmt.setString(3, event.getDescription().trim());
            stmt.setString(4, event.getCategory().trim());
            stmt.setString(5, event.getVenue().trim());
            stmt.setDate(6, Date.valueOf(event.getEventDate()));
            stmt.setTime(7, java.sql.Time.valueOf(event.getStartTime()));
            stmt.setTime(8, java.sql.Time.valueOf(event.getEndTime()));
            stmt.setTimestamp(9, java.sql.Timestamp.valueOf(event.getRegistrationDeadline()));

            if (event.getCapacity() != null) {
                stmt.setInt(10, event.getCapacity());
            } else {
                stmt.setNull(10, java.sql.Types.INTEGER);
            }

            if (event.getImageUrl() != null && !event.getImageUrl().isBlank()) {
                stmt.setString(11, event.getImageUrl().trim());
            } else {
                stmt.setNull(11, java.sql.Types.VARCHAR);
            }

            stmt.setInt(12, event.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating event id=" + event.getId(), e);
            throw new DataAccessException("Failed to update event", e);
        }
    }

    /**
     * Deletes an event by its ID. Cascade deletes registrations at the database level.
     *
     * @param id event ID
     * @return true if deleted
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM events WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting event id=" + id, e);
            throw new DataAccessException("Failed to delete event", e);
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
