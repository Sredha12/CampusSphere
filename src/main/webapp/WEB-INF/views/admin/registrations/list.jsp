<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="admin-page-container">
    <jsp:include page="/WEB-INF/views/admin/common/admin-nav.jsp"/>

    <div class="admin-header-row">
        <div>
            <div class="breadcrumbs">
                <a href="${pageContext.request.contextPath}/admin/dashboard">Admin</a> &rsaquo; <span>Registrations</span>
            </div>
            <h1 class="page-title">Event Registrations</h1>
            <p class="page-subtitle">View and filter student registration records and participation across campus activities.</p>
        </div>
    </div>

    <!-- Filter Controls -->
    <div class="filter-section">
        <form method="get" action="${pageContext.request.contextPath}/admin/registrations" class="filter-form">
            <div class="filter-group">
                <label for="event-filter" class="filter-label">Filter by Event</label>
                <select id="event-filter" name="eventId" class="form-control">
                    <option value="">All Events</option>
                    <c:forEach items="${events}" var="ev">
                        <option value="${ev.id}" ${selectedEventId eq ev.id ? 'selected' : ''}>
                            <c:out value="${ev.title}"/> (<c:out value="${ev.eventDate}"/>)
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="filter-group">
                <label for="status-filter" class="filter-label">Filter by Status</label>
                <select id="status-filter" name="status" class="form-control">
                    <option value="">All Statuses</option>
                    <option value="REGISTERED" ${selectedStatus eq 'REGISTERED' ? 'selected' : ''}>Registered</option>
                    <option value="CANCELLED" ${selectedStatus eq 'CANCELLED' ? 'selected' : ''}>Cancelled</option>
                </select>
            </div>

            <div class="filter-actions">
                <button type="submit" class="btn btn-primary">Apply Filters</button>
                <c:if test="${not empty selectedEventId or not empty selectedStatus}">
                    <a href="${pageContext.request.contextPath}/admin/registrations" class="btn btn-secondary">Clear Filters</a>
                </c:if>
            </div>
        </form>
    </div>

    <div class="results-meta">
        <span class="results-count">
            <c:choose>
                <c:when test="${hasMore}">
                    Showing latest 200 of <c:out value="${totalCount}"/> registrations.
                </c:when>
                <c:otherwise>
                    Showing <c:out value="${totalCount}"/> registration(s).
                </c:otherwise>
            </c:choose>
        </span>
    </div>

    <c:choose>
        <c:when test="${not empty registrations}">
            <div class="admin-table-card">
                <div class="table-responsive">
                    <table class="admin-table">
                        <thead>
                            <tr>
                                <th scope="col">Student</th>
                                <th scope="col">Event Title</th>
                                <th scope="col">Hosting Club</th>
                                <th scope="col">Registered At</th>
                                <th scope="col" class="text-center">Status</th>
                                <th scope="col" class="text-right">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${registrations}" var="reg">
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
                                               title="View event attendee roster">
                                                <c:out value="${reg.eventTitle}"/>
                                            </a>
                                        </div>
                                    </td>
                                    <td>
                                        <c:out value="${reg.clubName}"/>
                                    </td>
                                    <td>
                                        <div class="table-datetime-cell">
                                            <span><c:out value="${fn:substring(reg.registeredAt, 0, 19)}"/></span>
                                        </div>
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
                                               title="View Attendee Roster for <c:out value="${reg.eventTitle}"/>">
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
                <div class="empty-icon" aria-hidden="true">📋</div>
                <h3>No Registrations Found</h3>
                <p>
                    <c:choose>
                        <c:when test="${not empty selectedEventId or not empty selectedStatus}">
                            No registrations match your selected filter criteria.
                        </c:when>
                        <c:otherwise>
                            There are currently no event registrations recorded in the system.
                        </c:otherwise>
                    </c:choose>
                </p>
                <c:if test="${not empty selectedEventId or not empty selectedStatus}">
                    <a href="${pageContext.request.contextPath}/admin/registrations" class="btn btn-secondary">Reset Filters</a>
                </c:if>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
