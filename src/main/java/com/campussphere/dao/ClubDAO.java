package com.campussphere.dao;

import com.campussphere.model.Club;
import com.campussphere.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object providing read-only queries for clubs.
 */
public class ClubDAO {

    private static final Logger LOGGER = Logger.getLogger(ClubDAO.class.getName());

    /**
     * Retrieves all clubs with the count of upcoming events for each.
     */
    public List<Club> findAll() {
        String sql = "SELECT c.id, c.name, c.description, c.contact_email, c.image_url, " +
                "COUNT(CASE WHEN e.event_date >= CURDATE() THEN e.id END) AS upcoming_count " +
                "FROM clubs c " +
                "LEFT JOIN events e ON c.id = e.club_id " +
                "GROUP BY c.id, c.name, c.description, c.contact_email, c.image_url " +
                "ORDER BY c.name ASC";

        List<Club> clubs = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                clubs.add(mapClub(rs));
            }
            return clubs;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error querying all clubs", e);
            throw new DataAccessException("Failed to load clubs list", e);
        }
    }

    /**
     * Retrieves a single club by its unique ID.
     */
    public Club findById(int id) {
        String sql = "SELECT c.id, c.name, c.description, c.contact_email, c.image_url, " +
                "COUNT(CASE WHEN e.event_date >= CURDATE() THEN e.id END) AS upcoming_count " +
                "FROM clubs c " +
                "LEFT JOIN events e ON c.id = e.club_id " +
                "WHERE c.id = ? " +
                "GROUP BY c.id, c.name, c.description, c.contact_email, c.image_url";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapClub(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error querying club by id=" + id, e);
            throw new DataAccessException("Failed to load club details", e);
        }
    }

    /**
     * Retrieves featured clubs ordered by upcoming event count descending, then by name.
     */
    public List<Club> findFeatured(int limit) {
        String sql = "SELECT c.id, c.name, c.description, c.contact_email, c.image_url, " +
                "COUNT(CASE WHEN e.event_date >= CURDATE() THEN e.id END) AS upcoming_count " +
                "FROM clubs c " +
                "LEFT JOIN events e ON c.id = e.club_id " +
                "GROUP BY c.id, c.name, c.description, c.contact_email, c.image_url " +
                "ORDER BY upcoming_count DESC, c.name ASC " +
                "LIMIT ?";

        List<Club> clubs = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, Math.max(1, limit));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    clubs.add(mapClub(rs));
                }
            }
            return clubs;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error querying featured clubs", e);
            throw new DataAccessException("Failed to load featured clubs", e);
        }
    }

    /**
     * Retrieves all clubs with their total event count (for administration).
     */
    public List<Club> findAllWithEventCount() {
        String sql = "SELECT c.id, c.name, c.description, c.contact_email, c.image_url, " +
                "COUNT(e.id) AS total_events, " +
                "COUNT(CASE WHEN e.event_date >= CURDATE() THEN e.id END) AS upcoming_count " +
                "FROM clubs c " +
                "LEFT JOIN events e ON c.id = e.club_id " +
                "GROUP BY c.id, c.name, c.description, c.contact_email, c.image_url " +
                "ORDER BY c.name ASC";

        List<Club> clubs = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Club club = mapClub(rs);
                club.setTotalEventCount(rs.getInt("total_events"));
                clubs.add(club);
            }
            return clubs;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error querying all clubs with event counts", e);
            throw new DataAccessException("Failed to load clubs list for admin", e);
        }
    }

    /**
     * Counts all events belonging to a specific club.
     */
    public int countEventsByClubId(int clubId) {
        String sql = "SELECT COUNT(*) FROM events WHERE club_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, clubId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting events for clubId=" + clubId, e);
            throw new DataAccessException("Failed to count club events", e);
        }
    }

    /**
     * Checks if a club with the given name already exists (case-insensitive),
     * optionally excluding a specific club ID during updates.
     */
    public boolean existsByName(String name, Integer excludeId) {
        if (name == null || name.isBlank()) {
            return false;
        }
        String sql = "SELECT COUNT(*) FROM clubs WHERE LOWER(TRIM(name)) = LOWER(TRIM(?))" +
                (excludeId != null ? " AND id != ?" : "");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name.trim());
            if (excludeId != null) {
                stmt.setInt(2, excludeId);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
                return false;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking duplicate club name", e);
            throw new DataAccessException("Failed to check club name uniqueness", e);
        }
    }

    /**
     * Inserts a new club record.
     *
     * @param club the club to create
     * @return generated ID
     */
    public int create(Club club) {
        String sql = "INSERT INTO clubs (name, description, contact_email, image_url) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, club.getName().trim());
            stmt.setString(2, club.getDescription() != null ? club.getDescription().trim() : "");
            stmt.setString(3, club.getContactEmail() != null ? club.getContactEmail().trim() : "");
            stmt.setString(4, (club.getImageUrl() != null && !club.getImageUrl().isBlank()) ? club.getImageUrl().trim() : null);

            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    club.setId(id);
                    return id;
                }
            }
            throw new DataAccessException("Failed to retrieve generated key for created club");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error inserting new club", e);
            throw new DataAccessException("Failed to create club", e);
        }
    }

    /**
     * Updates an existing club record.
     *
     * @param club the club with updated values
     * @return true if updated, false if no record found
     */
    public boolean update(Club club) {
        String sql = "UPDATE clubs SET name = ?, description = ?, contact_email = ?, image_url = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, club.getName().trim());
            stmt.setString(2, club.getDescription() != null ? club.getDescription().trim() : "");
            stmt.setString(3, club.getContactEmail() != null ? club.getContactEmail().trim() : "");
            stmt.setString(4, (club.getImageUrl() != null && !club.getImageUrl().isBlank()) ? club.getImageUrl().trim() : null);
            stmt.setInt(5, club.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating club id=" + club.getId(), e);
            throw new DataAccessException("Failed to update club", e);
        }
    }

    /**
     * Deletes a club by its ID.
     *
     * @param id the club ID
     * @return true if deleted
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM clubs WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting club id=" + id, e);
            throw new DataAccessException("Failed to delete club", e);
        }
    }

    /**
     * Counts the total number of clubs in the database.
     *
     * @return count of clubs
     */
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM clubs";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting total clubs", e);
            throw new DataAccessException("Failed to count clubs", e);
        }
    }

    private Club mapClub(ResultSet rs) throws SQLException {
        Club club = new Club();
        club.setId(rs.getInt("id"));
        club.setName(rs.getString("name"));
        club.setDescription(rs.getString("description"));
        club.setContactEmail(rs.getString("contact_email"));
        club.setImageUrl(rs.getString("image_url"));
        club.setUpcomingEventCount(rs.getInt("upcoming_count"));
        return club;
    }
}
