<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="dashboard-wrapper">
    <div class="dashboard-header-card">
        <div class="dashboard-badge-row">
            <span class="badge badge-pill badge-primary-subtle">Student Portal</span>
        </div>
        <h1 class="dashboard-title">Welcome, <c:out value="${userName}"/>!</h1>
        <p class="dashboard-subtitle">Explore upcoming campus events, discover active clubs, and manage your student profile.</p>
    </div>

    <div class="dashboard-grid">
        <!-- Quick Navigation Cards -->
        <div class="dash-card">
            <div class="dash-card-icon" aria-hidden="true">🎉</div>
            <h2 class="dash-card-title">Campus Events</h2>
            <p class="dash-card-desc">Browse schedules, venues, and registration deadlines across all active student clubs.</p>
            <a href="${pageContext.request.contextPath}/events" class="btn btn-primary">Browse Events</a>
        </div>

        <div class="dash-card">
            <div class="dash-card-icon" aria-hidden="true">🏛️</div>
            <h2 class="dash-card-title">Clubs &amp; Societies</h2>
            <p class="dash-card-desc">Connect with technical guilds, cultural societies, and sports clubs active on campus.</p>
            <a href="${pageContext.request.contextPath}/clubs" class="btn btn-secondary">Explore Clubs</a>
        </div>

        <div class="dash-card">
            <div class="dash-card-icon" aria-hidden="true">👤</div>
            <h2 class="dash-card-title">My Profile</h2>
            <p class="dash-card-desc">View your registered student account information, department, and contact details.</p>
            <a href="${pageContext.request.contextPath}/student/profile" class="btn btn-secondary">View Profile</a>
        </div>
    </div>

    <!-- Honest Information Section about Upcoming Registrations -->
    <div class="info-notice-card">
        <div class="notice-icon" aria-hidden="true">📋</div>
        <div class="notice-content">
            <h3 class="notice-title">Event Registrations</h3>
            <p class="notice-text">
                Your event registration history and seat booking management will arrive in a subsequent build phase (Phase 7: Event Registration). You will be able to book event seats, view your confirmation status, and cancel tickets directly from this dashboard.
            </p>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
