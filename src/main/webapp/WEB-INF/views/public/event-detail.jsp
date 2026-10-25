<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="page-header">
    <div class="breadcrumbs">
        <a href="${pageContext.request.contextPath}/">Home</a> &rsaquo; 
        <a href="${pageContext.request.contextPath}/events">Events</a> &rsaquo; 
        <span><c:out value="${event.title}"/></span>
    </div>
</div>

<article class="event-detail-layout">
    <div class="event-main-content">
        <div class="detail-header-card">
            <div class="detail-badge-row">
                <span class="badge badge-category"><c:out value="${event.category}"/></span>
                <span class="badge badge-status badge-${fn:toLowerCase(event.availability)}">
                    <span class="status-dot status-dot-${fn:toLowerCase(event.availability)}" aria-hidden="true"></span>
                    <c:out value="${event.availabilityLabel}"/>
                </span>
            </div>

            <h1 class="detail-title"><c:out value="${event.title}"/></h1>

            <div class="detail-club-byline">
                Organized by: 
                <a href="${pageContext.request.contextPath}/clubs/detail?id=${event.clubId}" class="club-link">
                    <strong><c:out value="${event.clubName}"/></strong>
                </a>
            </div>
        </div>

        <c:choose>
            <c:when test="${not empty event.safeImageUrl}">
                <div class="detail-hero-media">
                    <img src="${pageContext.request.contextPath}${event.safeImageUrl}" alt="<c:out value="${event.title}"/>" class="detail-img">
                </div>
            </c:when>
            <c:otherwise>
                <div class="detail-category-hero category-media-${fn:toLowerCase(event.category)}">
                    <span class="category-hero-text"><c:out value="${event.category}"/> Event</span>
                </div>
            </c:otherwise>
        </c:choose>

        <section class="detail-body-card">
            <h2 class="section-title">About This Event</h2>
            <div class="event-description-text">
                <p><c:out value="${event.description}"/></p>
            </div>
        </section>
    </div>

    <!-- Sidebar with Event Logistics and Call to Action -->
    <aside class="event-sidebar">
        <div class="sidebar-card">
            <h3 class="sidebar-heading">Event Logistics</h3>

            <ul class="logistics-list">
                <li class="logistics-item">
                    <span class="logistics-icon" aria-hidden="true">📅</span>
                    <div>
                        <span class="logistics-label">Date</span>
                        <span class="logistics-value"><c:out value="${event.eventDate}"/></span>
                    </div>
                </li>
                <li class="logistics-item">
                    <span class="logistics-icon" aria-hidden="true">⏰</span>
                    <div>
                        <span class="logistics-label">Time</span>
                        <span class="logistics-value"><c:out value="${event.startTime}"/> - <c:out value="${event.endTime}"/></span>
                    </div>
                </li>
                <li class="logistics-item">
                    <span class="logistics-icon" aria-hidden="true">📍</span>
                    <div>
                        <span class="logistics-label">Venue</span>
                        <span class="logistics-value"><c:out value="${event.venue}"/></span>
                    </div>
                </li>
                <li class="logistics-item">
                    <span class="logistics-icon" aria-hidden="true">⏳</span>
                    <div>
                        <span class="logistics-label">Registration Deadline</span>
                        <span class="logistics-value"><c:out value="${event.registrationDeadline}"/></span>
                    </div>
                </li>
                <li class="logistics-item">
                    <span class="logistics-icon" aria-hidden="true">👥</span>
                    <div>
                        <span class="logistics-label">Capacity</span>
                        <span class="logistics-value">
                            <c:choose>
                                <c:when test="${event.capacity ne null}">
                                    <c:out value="${event.registeredCount}"/> / <c:out value="${event.capacity}"/> registered
                                    <span class="seats-subtext">(<strong><c:out value="${event.seatsLeft}"/></strong> seats left)</span>
                                </c:when>
                                <c:otherwise>
                                    <c:out value="${event.registeredCount}"/> registered (Unlimited Capacity)
                                </c:otherwise>
                            </c:choose>
                        </span>
                    </div>
                </li>
            </ul>

            <hr class="sidebar-divider">

            <!-- Honest Call to Action block (Registration arrives in Phase 5) -->
            <div class="cta-box">
                <h4>Event Registration</h4>
                <p class="cta-notice">
                    Register with your student account (login arrives in the next build phase).
                </p>
                <a href="${pageContext.request.contextPath}/login" class="btn btn-primary btn-block">
                    Sign In with Student Account
                </a>
            </div>
        </div>

        <div class="sidebar-card">
            <h4>Organizer Contact</h4>
            <p class="organizer-help">Need more information about this event? Contact the hosting club organizers:</p>
            <a href="${pageContext.request.contextPath}/clubs/detail?id=${event.clubId}" class="btn btn-secondary btn-block">
                View <c:out value="${event.clubName}"/> Profile
            </a>
        </div>
    </aside>
</article>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
