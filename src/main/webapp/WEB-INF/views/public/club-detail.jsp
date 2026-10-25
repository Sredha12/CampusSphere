<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="page-header">
    <div class="breadcrumbs">
        <a href="${pageContext.request.contextPath}/">Home</a> &rsaquo; 
        <a href="${pageContext.request.contextPath}/clubs">Clubs</a> &rsaquo; 
        <span><c:out value="${club.name}"/></span>
    </div>
</div>

<article class="club-profile-card">
    <div class="club-profile-header">
        <div class="club-avatar club-avatar-lg" aria-hidden="true">
            <c:out value="${fn:substring(club.name, 0, 1)}"/>
        </div>
        <div class="club-profile-intro">
            <h1 class="club-title"><c:out value="${club.name}"/></h1>
            <p class="club-email">
                Contact: <a href="mailto:<c:out value="${club.contactEmail}"/>"><c:out value="${club.contactEmail}"/></a>
            </p>
        </div>
    </div>

    <div class="club-profile-body">
        <h2 class="section-title">About the Society</h2>
        <p class="club-full-desc"><c:out value="${club.description}"/></p>
    </div>
</article>

<section class="section">
    <div class="section-header">
        <div>
            <h2 class="section-title">Upcoming Events by <c:out value="${club.name}"/></h2>
            <p class="section-subtitle">Events organized and hosted by this student club</p>
        </div>
    </div>

    <c:choose>
        <c:when test="${not empty upcomingEvents}">
            <div class="cards-grid">
                <c:forEach items="${upcomingEvents}" var="event">
                    <c:set var="event" value="${event}" scope="request"/>
                    <jsp:include page="/WEB-INF/views/common/event-card.jsp"/>
                </c:forEach>
            </div>
        </c:when>
        <c:otherwise>
            <div class="empty-state">
                <div class="empty-icon" aria-hidden="true">📅</div>
                <h3>No Upcoming Events for This Club</h3>
                <p>This club has not announced any upcoming events yet. Check back later or browse all events.</p>
                <a href="${pageContext.request.contextPath}/events" class="btn btn-secondary">Browse All Events</a>
            </div>
        </c:otherwise>
    </c:choose>
</section>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
