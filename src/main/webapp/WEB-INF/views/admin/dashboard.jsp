<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="dashboard-wrapper">
    <div class="dashboard-header-card admin-dashboard-header">
        <div class="dashboard-badge-row">
            <span class="badge badge-pill badge-category">Administration Portal</span>
        </div>
        <h1 class="dashboard-title">Welcome, <c:out value="${userName}"/>!</h1>
        <p class="dashboard-subtitle">CampusSphere Central Administrative Control and Event Oversight.</p>
    </div>

    <!-- Honest Information Section about upcoming Phase 6 & Phase 8 features -->
    <div class="info-notice-card admin-notice-card">
        <div class="notice-icon" aria-hidden="true">🛠️</div>
        <div class="notice-content">
            <h3 class="notice-title">Management Modules in Development</h3>
            <p class="notice-text">
                Full administrative management features are scheduled across upcoming implementation phases:
            </p>
            <ul class="admin-phase-list">
                <li><strong>Phase 6: Club &amp; Event Management:</strong> Create, edit, and deprecate student clubs, schedule events, configure venues, and set registration deadlines.</li>
                <li><strong>Phase 8: Attendee Management &amp; Analytics:</strong> View and export live attendee rosters, attendance metrics, and club performance reports.</li>
            </ul>
            <p class="notice-text">
                Currently, you can explore the public event catalog and club directories using the main navigation links above.
            </p>
        </div>
    </div>

    <div class="dashboard-grid">
        <div class="dash-card">
            <div class="dash-card-icon" aria-hidden="true">🎉</div>
            <h2 class="dash-card-title">Public Event Catalog</h2>
            <p class="dash-card-desc">Review events currently visible to campus students and external visitors.</p>
            <a href="${pageContext.request.contextPath}/events" class="btn btn-secondary">View Public Events</a>
        </div>

        <div class="dash-card">
            <div class="dash-card-icon" aria-hidden="true">🏛️</div>
            <h2 class="dash-card-title">Public Club Directory</h2>
            <p class="dash-card-desc">Inspect student clubs and societies registered in the campus directory.</p>
            <a href="${pageContext.request.contextPath}/clubs" class="btn btn-secondary">View Clubs</a>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
