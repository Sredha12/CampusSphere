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
}
