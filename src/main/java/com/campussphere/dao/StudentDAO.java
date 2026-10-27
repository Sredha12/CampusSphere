package com.campussphere.dao;

import com.campussphere.model.Student;
import com.campussphere.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object managing persistence and retrieval of {@link Student} records.
 */
public class StudentDAO {

    private static final Logger LOGGER = Logger.getLogger(StudentDAO.class.getName());
    private static final int MYSQL_ERR_DUPLICATE_ENTRY = 1062;

    /**
     * Finds a student by email address.
     * The email is automatically trimmed and converted to lowercase.
     *
     * @param email the email to search for
     * @return the matching Student, or null if not found
     */
    public Student findByEmail(String email) {
        if (email == null) {
            return null;
        }
        String normalizedEmail = email.trim().toLowerCase();

        String sql = "SELECT id, full_name, email, password_hash, department, year_of_study, phone, created_at " +
                "FROM students WHERE email = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, normalizedEmail);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapStudent(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error retrieving student by email", e);
            throw new DataAccessException("Failed to query student by email", e);
        }
    }

    /**
     * Finds a student by primary key ID.
     *
     * @param id the student ID
     * @return the matching Student, or null if not found
     */
    public Student findById(int id) {
        String sql = "SELECT id, full_name, email, password_hash, department, year_of_study, phone, created_at " +
                "FROM students WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapStudent(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error retrieving student by id=" + id, e);
            throw new DataAccessException("Failed to query student by id", e);
        }
    }

    /**
     * Inserts a new student record into the database.
     * Email is trimmed and lowercased before saving.
     *
     * @param student the student entity containing full name, email, password hash, etc.
     * @return the newly generated student ID
     * @throws DuplicateEmailException if a record with the same email already exists (MySQL code 1062)
     * @throws DataAccessException     on any other database failure
     */
    public int insert(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("Student entity cannot be null");
        }

        String normalizedEmail = (student.getEmail() != null) ? student.getEmail().trim().toLowerCase() : "";

        String sql = "INSERT INTO students (full_name, email, password_hash, department, year_of_study, phone) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, student.getFullName() != null ? student.getFullName().trim() : "");
            stmt.setString(2, normalizedEmail);
            stmt.setString(3, student.getPasswordHash());
            stmt.setString(4, student.getDepartment() != null ? student.getDepartment().trim() : "");
            stmt.setInt(5, student.getYearOfStudy());
            stmt.setString(6, student.getPhone() != null ? student.getPhone().trim() : "");

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int newId = generatedKeys.getInt(1);
                    student.setId(newId);
                    student.setEmail(normalizedEmail);
                    return newId;
                } else {
                    throw new DataAccessException("Student insert succeeded but no generated ID was returned");
                }
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == MYSQL_ERR_DUPLICATE_ENTRY) {
                LOGGER.log(Level.INFO, "Duplicate student email rejected: {0}", normalizedEmail);
                throw new DuplicateEmailException("An account with this email address already exists.", e);
            }
            LOGGER.log(Level.SEVERE, "Database error inserting new student", e);
            throw new DataAccessException("Failed to create student account", e);
        }
    }

    /**
     * Updates profile fields for an existing student.
     *
     * @param id          student ID
     * @param fullName    updated full name
     * @param department  updated department
     * @param yearOfStudy updated year of study (1-5)
     * @param phone       updated phone number
     * @return true if 1 row was updated, false otherwise
     */
    public boolean updateProfile(int id, String fullName, String department, int yearOfStudy, String phone) {
        String sql = "UPDATE students SET full_name = ?, department = ?, year_of_study = ?, phone = ? " +
                "WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, fullName != null ? fullName.trim() : "");
            stmt.setString(2, department != null ? department.trim() : "");
            stmt.setInt(3, yearOfStudy);
            stmt.setString(4, phone != null ? phone.trim() : "");
            stmt.setInt(5, id);

            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated == 1;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error updating profile for student id=" + id, e);
            throw new DataAccessException("Failed to update student profile", e);
        }
    }

    private Student mapStudent(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setId(rs.getInt("id"));
        s.setFullName(rs.getString("full_name"));
        s.setEmail(rs.getString("email"));
        s.setPasswordHash(rs.getString("password_hash"));
        s.setDepartment(rs.getString("department"));
        s.setYearOfStudy(rs.getInt("year_of_study"));
        s.setPhone(rs.getString("phone"));
        s.setCreatedAt(rs.getTimestamp("created_at"));
        return s;
    }
}
