package com.campussphere.model;

import com.campussphere.config.AppConfig;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;

/**
 * Domain entity representing a student's registration for a campus event.
 */
public class EventRegistration implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String STATUS_REGISTERED = "REGISTERED";
    public static final String STATUS_CANCELLED = "CANCELLED";

    private int id;
    private int studentId;
    private int eventId;
    private Timestamp registeredAt;
    private String status;

    // Joined event fields for display
    private String eventTitle;
    private int clubId;
    private String clubName;
    private String category;
    private String venue;
    private LocalDate eventDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDateTime registrationDeadline;
    private Integer capacity;
    private String imageUrl;
    private AvailabilityStatus availability;
    private String availabilityLabel;

    // Joined student fields for display (roster, admin registrations list)
    private String studentName;
    private String studentEmail;
    private String studentDepartment;
    private Integer studentYearOfStudy;
    private String studentPhone;

    public EventRegistration() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public Timestamp getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(Timestamp registeredAt) {
        this.registeredAt = registeredAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isRegistered() {
        return STATUS_REGISTERED.equalsIgnoreCase(status);
    }

    public boolean isCancelled() {
        return STATUS_CANCELLED.equalsIgnoreCase(status);
    }

    /**
     * Determines whether this registration can currently be cancelled by the student.
     * Cancellation is disallowed once the event has started or ended.
     */
    public boolean isCancellable() {
        if (!isRegistered()) {
            return false;
        }
        if (eventDate == null) {
            return true;
        }
        LocalTime time = (startTime != null) ? startTime : LocalTime.MIN;
        ZonedDateTime now = ZonedDateTime.now(AppConfig.getInstance().getZoneId());
        ZonedDateTime eventStart = LocalDateTime.of(eventDate, time).atZone(AppConfig.getInstance().getZoneId());
        return now.isBefore(eventStart);
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public int getClubId() {
        return clubId;
    }

    public void setClubId(int clubId) {
        this.clubId = clubId;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public LocalDateTime getRegistrationDeadline() {
        return registrationDeadline;
    }

    public void setRegistrationDeadline(LocalDateTime registrationDeadline) {
        this.registrationDeadline = registrationDeadline;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSafeImageUrl() {
        if (imageUrl != null && (imageUrl.startsWith("/") || imageUrl.startsWith("assets/"))) {
            return imageUrl.startsWith("/") ? imageUrl : "/" + imageUrl;
        }
        return null;
    }

    public AvailabilityStatus getAvailability() {
        return availability;
    }

    public void setAvailability(AvailabilityStatus availability) {
        this.availability = availability;
    }

    public String getAvailabilityLabel() {
        return availabilityLabel;
    }

    public void setAvailabilityLabel(String availabilityLabel) {
        this.availabilityLabel = availabilityLabel;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public String getStudentDepartment() {
        return studentDepartment;
    }

    public void setStudentDepartment(String studentDepartment) {
        this.studentDepartment = studentDepartment;
    }

    public Integer getStudentYearOfStudy() {
        return studentYearOfStudy;
    }

    public void setStudentYearOfStudy(Integer studentYearOfStudy) {
        this.studentYearOfStudy = studentYearOfStudy;
    }

    public String getStudentPhone() {
        return studentPhone;
    }

    public void setStudentPhone(String studentPhone) {
        this.studentPhone = studentPhone;
    }
}
