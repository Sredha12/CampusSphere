package com.campussphere.service;

import com.campussphere.dao.ClubDAO;
import com.campussphere.model.Club;

import java.util.List;

/**
 * Business service handling club directory and details retrieval.
 */
public class ClubService {

    private final ClubDAO clubDAO;

    public ClubService() {
        this.clubDAO = new ClubDAO();
    }

    public ClubService(ClubDAO clubDAO) {
        this.clubDAO = clubDAO;
    }

    /**
     * Returns the total count of clubs in the database.
     */
    public int getTotalClubCount() {
        return clubDAO.countAll();
    }

    /**
     * Retrieves all clubs with upcoming event counts.
     */
    public List<Club> getAllClubs() {
        return clubDAO.findAll();
    }

    /**
     * Retrieves a club by its ID.
     */
    public Club getClubById(int id) {
        return clubDAO.findById(id);
    }

    /**
     * Retrieves featured clubs for the homepage.
     */
    public List<Club> getFeaturedClubs(int limit) {
        return clubDAO.findFeatured(limit);
    }

    /**
     * Retrieves all clubs with total event counts for administration.
     */
    public List<Club> getAllClubsForAdmin() {
        return clubDAO.findAllWithEventCount();
    }

    /**
     * Returns the total number of events hosted by a club.
     */
    public int getEventCountForClub(int clubId) {
        return clubDAO.countEventsByClubId(clubId);
    }

    /**
     * Checks if a club name is already in use.
     */
    public boolean isClubNameDuplicate(String name, Integer excludeId) {
        return clubDAO.existsByName(name, excludeId);
    }

    /**
     * Creates a new club.
     */
    public int createClub(Club club) {
        return clubDAO.create(club);
    }

    /**
     * Updates an existing club.
     */
    public boolean updateClub(Club club) {
        return clubDAO.update(club);
    }

    /**
     * Deletes a club if it has no associated events.
     *
     * @param clubId the club ID
     * @return true if deleted
     * @throws IllegalStateException if the club still has events
     */
    public boolean deleteClub(int clubId) {
        int eventCount = clubDAO.countEventsByClubId(clubId);
        if (eventCount > 0) {
            throw new IllegalStateException("This club has " + eventCount + " event(s). Delete or move those events first.");
        }
        return clubDAO.delete(clubId);
    }
}
