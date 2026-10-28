<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="dashboard-wrapper">
    <div class="dashboard-header-card">
        <div class="dashboard-badge-row">
            <span class="badge badge-pill badge-primary-subtle">Student Portal</span>
        </div>
        <h1 class="dashboard-title">Welcome, <c:out value="${userName}"/>!</h1>
        <p class="dashboard-subtitle">Explore upcoming campus events, discover active clubs, and manage your student registrations.</p>
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

    <!-- Registered Events Section -->
    <div class="dashboard-section">
        <div class="section-header">
            <div>
                <h2 class="section-title">My Registered Events</h2>
                <p class="section-subtitle">Track your upcoming event registrations, schedules, and venues.</p>
            </div>
            <a href="${pageContext.request.contextPath}/events" class="btn btn-sm btn-secondary">
                Browse More Events
            </a>
        </div>

        <c:choose>
            <c:when test="${not empty registrations}">
                <div class="admin-table-card">
                    <div class="table-responsive">
                        <table class="admin-table" aria-label="My Registered Events">
                            <thead>
                                <tr>
                                    <th scope="col">Event</th>
                                    <th scope="col">Club</th>
                                    <th scope="col">Schedule</th>
                                    <th scope="col">Venue</th>
                                    <th scope="col">Status</th>
                                    <th scope="col" class="text-right">Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="reg" items="${registrations}">
                                    <tr>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/events/detail?id=${reg.eventId}" class="table-item-link">
                                                <c:out value="${reg.eventTitle}"/>
                                            </a>
                                            <div class="table-submeta">
                                                <span class="badge badge-pill badge-primary-subtle"><c:out value="${reg.category}"/></span>
                                            </div>
                                        </td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/clubs/detail?id=${reg.clubId}" class="table-email-link">
                                                <c:out value="${reg.clubName}"/>
                                            </a>
                                        </td>
                                        <td>
                                            <div class="table-datetime-cell">
                                                <strong><c:out value="${reg.eventDate}"/></strong>
                                                <span class="text-muted">
                                                    <c:out value="${reg.startTime}"/>
                                                    <c:if test="${not empty reg.endTime}"> - <c:out value="${reg.endTime}"/></c:if>
                                                </span>
                                            </div>
                                        </td>
                                        <td>
                                            <c:out value="${reg.venue}"/>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${reg.registered}">
                                                    <span class="badge badge-status badge-open">
                                                        <span class="status-dot status-dot-open" aria-hidden="true"></span>
                                                        Registered
                                                    </span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge badge-status badge-ended">
                                                        Cancelled
                                                    </span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td class="text-right">
                                            <div class="action-btn-group">
                                                <c:choose>
                                                    <c:when test="${reg.registered}">
                                                        <c:choose>
                                                            <c:when test="${reg.cancellable}">
                                                                <a href="${pageContext.request.contextPath}/student/events/cancel-confirm?id=${reg.eventId}" class="btn btn-sm btn-secondary">
                                                                    Cancel
                                                                </a>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="text-muted" title="Event has already started or ended">Started</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <a href="${pageContext.request.contextPath}/events/detail?id=${reg.eventId}" class="btn btn-sm btn-ghost">
                                                            View
                                                        </a>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <div class="empty-state">
                    <div class="empty-icon" aria-hidden="true">🎫</div>
                    <h3>No Event Registrations Yet</h3>
                    <p>You haven't registered for any campus events yet. Explore upcoming workshops, seminars, and competitions!</p>
                    <a href="${pageContext.request.contextPath}/events" class="btn btn-primary">
                        Browse Upcoming Events
                    </a>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
