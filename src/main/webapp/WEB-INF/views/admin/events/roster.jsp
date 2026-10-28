<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="admin-page-container">
    <jsp:include page="/WEB-INF/views/admin/common/admin-nav.jsp"/>

    <div class="admin-header-row">
        <div>
            <div class="breadcrumbs">
                <a href="${pageContext.request.contextPath}/admin/dashboard">Admin</a> &rsaquo; 
                <a href="${pageContext.request.contextPath}/admin/events">Events</a> &rsaquo; 
                <span>Attendee Roster</span>
            </div>
            <h1 class="page-title">Attendee Roster: <c:out value="${event.title}"/></h1>
            <p class="page-subtitle">Official verified list of registered student attendees for this campus event.</p>
        </div>
        <div class="admin-header-actions">
            <button type="button" class="btn btn-primary" onclick="window.print()" id="btn-print-roster">
                <span aria-hidden="true">🖨️</span> Print Roster
            </button>
            <a href="${pageContext.request.contextPath}/admin/registrations?eventId=${event.id}" class="btn btn-secondary">
                Registrations
            </a>
            <a href="${pageContext.request.contextPath}/admin/events" class="btn btn-ghost">
                &larr; Back to Events
            </a>
        </div>
    </div>

    <!-- Event Summary Information -->
    <div class="roster-summary-card">
        <div class="roster-header">
            <span class="badge badge-sm badge-category"><c:out value="${event.category}"/></span>
            <h2 class="section-title" style="margin-top: 0.5rem;"><c:out value="${event.title}"/></h2>
        </div>

        <div class="roster-details-grid">
            <div class="roster-detail-item">
                <span class="roster-detail-label">Hosting Club</span>
                <span class="roster-detail-value"><c:out value="${event.clubName}"/></span>
            </div>
            <div class="roster-detail-item">
                <span class="roster-detail-label">Date &amp; Time</span>
                <span class="roster-detail-value">
                    <c:out value="${event.eventDate}"/> (<c:out value="${event.startTime}"/> - <c:out value="${event.endTime}"/>)
                </span>
            </div>
            <div class="roster-detail-item">
                <span class="roster-detail-label">Venue</span>
                <span class="roster-detail-value"><c:out value="${event.venue}"/></span>
            </div>
            <div class="roster-detail-item">
                <span class="roster-detail-label">Capacity</span>
                <span class="roster-detail-value">
                    <c:choose>
                        <c:when test="${event.capacity ne null}">
                            <c:out value="${event.capacity}"/> Seats
                        </c:when>
                        <c:otherwise>
                            Unlimited
                        </c:otherwise>
                    </c:choose>
                </span>
            </div>
            <div class="roster-detail-item">
                <span class="roster-detail-label">Registered Attendees</span>
                <span class="roster-detail-value">
                    <c:out value="${event.registeredCount}"/> active
                </span>
            </div>
        </div>
    </div>

    <!-- Attendees Table -->
    <c:choose>
        <c:when test="${not empty attendees}">
            <div class="admin-table-card">
                <div class="table-responsive">
                    <table class="admin-table" id="roster-table">
                        <thead>
                            <tr>
                                <th scope="col" style="width: 50px;" class="text-center">#</th>
                                <th scope="col">Student Name</th>
                                <th scope="col">Email Address</th>
                                <th scope="col">Department</th>
                                <th scope="col" class="text-center">Year of Study</th>
                                <th scope="col">Phone</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${attendees}" var="att" varStatus="loop">
                                <tr>
                                    <td class="text-center">
                                        <c:out value="${loop.count}"/>
                                    </td>
                                    <td>
                                        <strong><c:out value="${att.studentName}"/></strong>
                                    </td>
                                    <td>
                                        <c:out value="${att.studentEmail}"/>
                                    </td>
                                    <td>
                                        <c:out value="${att.studentDepartment}"/>
                                    </td>
                                    <td class="text-center">
                                        Year <c:out value="${att.studentYearOfStudy}"/>
                                    </td>
                                    <td>
                                        <c:out value="${att.studentPhone}"/>
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
                <div class="empty-icon" aria-hidden="true">👥</div>
                <h3>No Registered Attendees</h3>
                <p>There are currently no active students registered for this event.</p>
                <a href="${pageContext.request.contextPath}/admin/events" class="btn btn-secondary">Return to Events</a>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
