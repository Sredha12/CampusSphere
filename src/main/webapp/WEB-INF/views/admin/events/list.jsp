<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="admin-page-container">
    <jsp:include page="/WEB-INF/views/admin/common/admin-nav.jsp"/>

    <div class="admin-header-row">
        <div>
            <div class="breadcrumbs">
                <a href="${pageContext.request.contextPath}/admin/dashboard">Admin</a> &rsaquo; <span>Events</span>
            </div>
            <h1 class="page-title">Manage Campus Events</h1>
            <p class="page-subtitle">Oversee event schedules, capacities, venues, and registration deadlines across all clubs.</p>
        </div>
        <div class="admin-header-actions">
            <a href="${pageContext.request.contextPath}/admin/events/new" class="btn btn-primary">
                <span aria-hidden="true">➕</span> Add Event
            </a>
        </div>
    </div>

    <c:choose>
        <c:when test="${not empty events}">
            <div class="admin-table-card">
                <div class="table-responsive">
                    <table class="admin-table">
                        <thead>
                            <tr>
                                <th scope="col">Event Details</th>
                                <th scope="col">Hosting Club</th>
                                <th scope="col">Date &amp; Time</th>
                                <th scope="col">Registration Deadline</th>
                                <th scope="col" class="text-center">Capacity</th>
                                <th scope="col" class="text-center">Registered</th>
                                <th scope="col" class="text-center">Availability</th>
                                <th scope="col" class="text-right">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${events}" var="event">
                                <tr>
                                    <td>
                                        <div class="event-table-info">
                                            <strong>
                                                <a href="${pageContext.request.contextPath}/events/detail?id=${event.id}" 
                                                   class="table-item-link" 
                                                   target="_blank" 
                                                   title="View public event page">
                                                    <c:out value="${event.title}"/>
                                                </a>
                                            </strong>
                                            <div class="table-submeta">
                                                <span class="badge badge-sm badge-category"><c:out value="${event.category}"/></span>
                                                <span class="text-muted"><c:out value="${event.venue}"/></span>
                                            </div>
                                        </div>
                                    </td>
                                    <td>
                                        <c:out value="${event.clubName}"/>
                                    </td>
                                    <td>
                                        <div class="table-datetime-cell">
                                            <span><c:out value="${event.eventDate}"/></span>
                                            <small class="text-muted"><c:out value="${event.startTime}"/> - <c:out value="${event.endTime}"/></small>
                                        </div>
                                    </td>
                                    <td>
                                        <small><c:out value="${event.registrationDeadline}"/></small>
                                    </td>
                                    <td class="text-center">
                                        <c:choose>
                                            <c:when test="${event.capacity ne null}">
                                                <c:out value="${event.capacity}"/>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge badge-secondary badge-sm">Unlimited</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="text-center">
                                        <a href="${pageContext.request.contextPath}/admin/events/roster?id=${event.id}" 
                                           class="table-item-link" 
                                           title="View Attendee Roster">
                                            <strong><c:out value="${event.registeredCount}"/></strong>
                                        </a>
                                    </td>
                                    <td class="text-center">
                                        <span class="badge badge-status badge-${fn:toLowerCase(event.availability)}">
                                            <span class="status-dot status-dot-${fn:toLowerCase(event.availability)}" aria-hidden="true"></span>
                                            <c:out value="${event.availabilityLabel}"/>
                                        </span>
                                    </td>
                                    <td class="text-right">
                                        <div class="action-btn-group">
                                            <a href="${pageContext.request.contextPath}/admin/events/roster?id=${event.id}" 
                                               class="btn btn-sm btn-secondary" 
                                               title="View Attendee Roster for <c:out value="${event.title}"/>">
                                                Roster
                                            </a>
                                            <a href="${pageContext.request.contextPath}/admin/registrations?eventId=${event.id}" 
                                               class="btn btn-sm btn-secondary" 
                                               title="View Registrations for <c:out value="${event.title}"/>">
                                                Registrations
                                            </a>
                                            <a href="${pageContext.request.contextPath}/admin/events/edit?id=${event.id}" 
                                               class="btn btn-sm btn-secondary" 
                                               title="Edit Event <c:out value="${event.title}"/>">
                                                Edit
                                            </a>
                                            <a href="${pageContext.request.contextPath}/admin/events/delete-confirm?id=${event.id}" 
                                               class="btn btn-sm btn-danger" 
                                               title="Delete Event <c:out value="${event.title}"/>">
                                                Delete
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
                <div class="empty-icon" aria-hidden="true">📅</div>
                <h3>No Events Scheduled</h3>
                <p>There are currently no events registered in the system.</p>
                <a href="${pageContext.request.contextPath}/admin/events/new" class="btn btn-primary">Schedule the First Event</a>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
