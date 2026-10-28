<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="page-header">
    <div class="breadcrumbs">
        <a href="${pageContext.request.contextPath}/">Home</a> &rsaquo;
        <a href="${pageContext.request.contextPath}/student/dashboard">Student Dashboard</a> &rsaquo;
        <span>Cancel Registration</span>
    </div>
</div>

<div class="auth-page-wrapper">
    <div class="admin-confirm-card">
        <div class="confirm-card-header">
            <span class="badge badge-status badge-closed">Confirm Cancellation</span>
            <h1 class="confirm-card-title">Cancel Event Registration</h1>
            <p class="confirm-card-subtitle">
                Are you sure you want to cancel your registration? Your reserved seat will be released immediately and made available to other students.
            </p>
        </div>

        <div class="confirm-details-box">
            <span class="confirm-entity-name"><c:out value="${registration.eventTitle}"/></span>
            <div class="confirm-meta">
                <strong>Hosting Club:</strong> <c:out value="${registration.clubName}"/>
            </div>
            <div class="confirm-meta">
                <strong>Schedule:</strong> <c:out value="${registration.eventDate}"/> at <c:out value="${registration.startTime}"/>
                <c:if test="${not empty registration.endTime}"> - <c:out value="${registration.endTime}"/></c:if>
            </div>
            <div class="confirm-meta">
                <strong>Venue:</strong> <c:out value="${registration.venue}"/>
            </div>
        </div>

        <form action="${pageContext.request.contextPath}/student/events/cancel" method="POST" class="confirm-actions">
            <input type="hidden" name="_csrf" value="${csrfToken}"/>
            <input type="hidden" name="eventId" value="${registration.eventId}"/>
            <input type="hidden" name="redirect" value="dashboard"/>

            <button type="submit" class="btn btn-danger">
                Yes, Cancel Registration
            </button>
            <a href="${pageContext.request.contextPath}/student/dashboard" class="btn btn-secondary">
                Keep Registration
            </a>
        </form>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
