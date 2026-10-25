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
