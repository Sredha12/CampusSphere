<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="dashboard-wrapper">
    <jsp:include page="/WEB-INF/views/admin/common/admin-nav.jsp"/>

    <div class="dashboard-header-card admin-dashboard-header">
        <div class="dashboard-badge-row">
            <span class="badge badge-pill badge-category">Administration Portal</span>
        </div>
        <h1 class="dashboard-title">Welcome, <c:out value="${userName}"/>!</h1>
        <p class="dashboard-subtitle">CampusSphere Central Administrative Control and Event Oversight.</p>
    </div>

    <!-- Platform Statistics Overview -->
    <div class="stats-grid">
        <a href="${pageContext.request.contextPath}/admin/clubs" class="stat-card">
            <div class="stat-card-header">
                <span class="stat-label">Total Clubs</span>
                <span class="stat-card-icon" aria-hidden="true">🏛️</span>
            </div>
            <div class="stat-value"><c:out value="${totalClubs}"/></div>
            <span class="stat-desc">Active clubs &amp; societies</span>
        </a>

        <a href="${pageContext.request.contextPath}/admin/events" class="stat-card">
            <div class="stat-card-header">
                <span class="stat-label">Total Events</span>
                <span class="stat-card-icon" aria-hidden="true">📅</span>
            </div>
            <div class="stat-value"><c:out value="${totalEvents}"/></div>
            <span class="stat-desc">All scheduled campus events</span>
        </a>

        <a href="${pageContext.request.contextPath}/admin/events" class="stat-card">
            <div class="stat-card-header">
                <span class="stat-label">Upcoming Events</span>
                <span class="stat-card-icon" aria-hidden="true">⏳</span>
            </div>
            <div class="stat-value"><c:out value="${upcomingEvents}"/></div>
            <span class="stat-desc">Events starting after now</span>
        </a>

        <div class="stat-card">
            <div class="stat-card-header">
                <span class="stat-label">Total Students</span>
                <span class="stat-card-icon" aria-hidden="true">👥</span>
            </div>
            <div class="stat-value"><c:out value="${totalStudents}"/></div>
            <span class="stat-desc">Registered student accounts</span>
        </div>

        <a href="${pageContext.request.contextPath}/admin/registrations?status=REGISTERED" class="stat-card">
            <div class="stat-card-header">
                <span class="stat-label">Active Registrations</span>
                <span class="stat-card-icon" aria-hidden="true">✅</span>
            </div>
            <div class="stat-value"><c:out value="${activeRegistrations}"/></div>
            <span class="stat-desc">Status: Registered</span>
        </a>

        <a href="${pageContext.request.contextPath}/admin/registrations?status=CANCELLED" class="stat-card">
            <div class="stat-card-header">
                <span class="stat-label">Cancelled</span>
                <span class="stat-card-icon" aria-hidden="true">❌</span>
            </div>
            <div class="stat-value"><c:out value="${cancelledRegistrations}"/></div>
            <span class="stat-desc">Status: Cancelled</span>
        </a>
    </div>

    <!-- Active Management Modules -->
    <div class="dashboard-grid" style="margin-bottom: var(--space-8);">
        <div class="dash-card">
            <div class="dash-card-icon" aria-hidden="true">🏛️</div>
            <h2 class="dash-card-title">Club Management</h2>
            <p class="dash-card-desc">Create, view, update, and manage student clubs and societies.</p>
            <div class="dash-card-actions">
                <a href="${pageContext.request.contextPath}/admin/clubs" class="btn btn-primary btn-sm">Manage Clubs</a>
                <a href="${pageContext.request.contextPath}/admin/clubs/new" class="btn btn-secondary btn-sm">Add Club</a>
            </div>
        </div>

        <div class="dash-card">
            <div class="dash-card-icon" aria-hidden="true">📅</div>
            <h2 class="dash-card-title">Event Management</h2>
            <p class="dash-card-desc">Schedule campus events, configure venues, registration deadlines, and seat capacity.</p>
            <div class="dash-card-actions">
                <a href="${pageContext.request.contextPath}/admin/events" class="btn btn-primary btn-sm">Manage Events</a>
                <a href="${pageContext.request.contextPath}/admin/events/new" class="btn btn-secondary btn-sm">Add Event</a>
            </div>
        </div>

        <div class="dash-card">
            <div class="dash-card-icon" aria-hidden="true">📝</div>
            <h2 class="dash-card-title">Registration Management</h2>
            <p class="dash-card-desc">Review student participation, filter registrations by event, and view printable attendee rosters.</p>
            <div class="dash-card-actions">
                <a href="${pageContext.request.contextPath}/admin/registrations" class="btn btn-primary btn-sm">View Registrations</a>
            </div>
        </div>
    </div>

    <!-- Recent Registrations Overview -->
    <div style="margin-bottom: var(--space-8);">
        <div class="section-header">
            <div>
                <h2 class="section-title">Recent Registrations</h2>
                <p class="section-subtitle">Latest student event sign-ups and status changes</p>
            </div>
            <a href="${pageContext.request.contextPath}/admin/registrations" class="link-arrow">View all registrations &rarr;</a>
        </div>

        <c:choose>
            <c:when test="${not empty recentRegistrations}">
                <div class="admin-table-card">
                    <div class="table-responsive">
                        <table class="admin-table">
                            <thead>
                                <tr>
                                    <th scope="col">Student</th>
                                    <th scope="col">Event Title</th>
                                    <th scope="col">Registration Time</th>
                                    <th scope="col" class="text-center">Status</th>
                                    <th scope="col" class="text-right">Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${recentRegistrations}" var="reg">
                                    <tr>
                                        <td>
                                            <div class="event-table-info">
                                                <strong><c:out value="${reg.studentName}"/></strong>
                                                <span class="text-muted"><c:out value="${reg.studentEmail}"/></span>
                                            </div>
                                        </td>
                                        <td>
                                            <div class="event-table-info">
                                                <a href="${pageContext.request.contextPath}/admin/events/roster?id=${reg.eventId}" 
                                                   class="table-item-link" 
                                                   title="View attendee roster">
                                                    <c:out value="${reg.eventTitle}"/>
                                                </a>
                                                <span class="text-muted" style="font-size: 0.85rem;"><c:out value="${reg.clubName}"/></span>
                                            </div>
                                        </td>
                                        <td>
                                            <span class="text-muted"><c:out value="${fn:substring(reg.registeredAt, 0, 19)}"/></span>
                                        </td>
                                        <td class="text-center">
                                            <c:choose>
                                                <c:when test="${reg.status eq 'REGISTERED'}">
                                                    <span class="badge badge-status badge-registered">
                                                        <span class="status-dot status-dot-registered" aria-hidden="true"></span>
                                                        Registered
                                                    </span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge badge-status badge-cancelled">
                                                        <span class="status-dot status-dot-cancelled" aria-hidden="true"></span>
                                                        Cancelled
                                                    </span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td class="text-right">
                                            <div class="action-btn-group">
                                                <a href="${pageContext.request.contextPath}/admin/events/roster?id=${reg.eventId}" 
                                                   class="btn btn-sm btn-secondary" 
                                                   title="View Attendee Roster">
                                                    Roster
                                                </a>
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
                    <div class="empty-icon" aria-hidden="true">📝</div>
                    <h3>No Registrations Yet</h3>
                    <p>There are no student registrations recorded in the system yet.</p>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

    <!-- Quick Public View Links -->
    <div class="dashboard-grid">
        <div class="dash-card">
            <div class="dash-card-icon" aria-hidden="true">🎉</div>
            <h2 class="dash-card-title">Public Event Catalog</h2>
            <p class="dash-card-desc">Review events currently visible to campus students and public visitors.</p>
            <a href="${pageContext.request.contextPath}/events" class="btn btn-secondary">View Public Events</a>
        </div>

        <div class="dash-card">
            <div class="dash-card-icon" aria-hidden="true">🌐</div>
            <h2 class="dash-card-title">Public Club Directory</h2>
            <p class="dash-card-desc">Inspect student clubs and societies currently registered in the public directory.</p>
            <a href="${pageContext.request.contextPath}/clubs" class="btn btn-secondary">View Public Clubs</a>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
