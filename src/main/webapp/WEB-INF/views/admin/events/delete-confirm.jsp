<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="admin-page-container">
    <jsp:include page="/WEB-INF/views/admin/common/admin-nav.jsp"/>

    <div class="admin-confirm-card">
        <div class="confirm-card-header">
            <div class="breadcrumbs">
                <a href="${pageContext.request.contextPath}/admin/dashboard">Admin</a> &rsaquo; 
                <a href="${pageContext.request.contextPath}/admin/events">Events</a> &rsaquo; 
                <span>Delete Confirmation</span>
            </div>
            <h1 class="confirm-card-title">Delete Event Confirmation</h1>
        </div>

        <div class="confirm-details-box">
            <h2 class="confirm-entity-name"><c:out value="${event.title}"/></h2>
            <p class="confirm-meta"><strong>Hosting Club:</strong> <c:out value="${event.clubName}"/></p>
            <p class="confirm-meta"><strong>Schedule:</strong> <c:out value="${event.eventDate}"/> (<c:out value="${event.startTime}"/> - <c:out value="${event.endTime}"/>)</p>
            <p class="confirm-meta"><strong>Venue:</strong> <c:out value="${event.venue}"/></p>
            <p class="confirm-meta"><strong>Total Registrations to be Deleted:</strong> <c:out value="${registrationCount}"/></p>
        </div>

        <div class="alert alert-warning" role="alert">
            <span class="alert-icon" aria-hidden="true">⚠</span>
            <div class="alert-text">
                <strong>Permanent Action Warning:</strong> Deleting this event will also permanently delete 
                <strong><c:out value="${registrationCount}"/></strong> registered attendee record(s) via database cascade. This operation cannot be reversed.
            </div>
        </div>

        <form action="${pageContext.request.contextPath}/admin/events/delete" method="post" class="confirm-form">
            <input type="hidden" name="_csrf" value="${csrfToken}">
            <input type="hidden" name="id" value="${event.id}">

            <div class="confirm-actions">
                <button type="submit" class="btn btn-danger">
                    Permanently Delete Event
                </button>
                <a href="${pageContext.request.contextPath}/admin/events" class="btn btn-secondary">
                    Cancel
                </a>
            </div>
        </form>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
