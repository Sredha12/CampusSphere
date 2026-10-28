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

            <!-- Dynamic Call to Action: Registration & Cancellation -->
            <div class="cta-box">
                <h4>Event Registration</h4>
                <c:choose>
                    <c:when test="${isRegistered}">
                        <div class="registered-status-box">
                            <span class="badge badge-status badge-open">
                                <span class="status-dot status-dot-open" aria-hidden="true"></span>
                                You Are Registered
                            </span>
                            <p class="registered-status-text">
                                You hold an active seat reservation for this event.
                            </p>
                            <a href="${pageContext.request.contextPath}/student/events/cancel-confirm?id=${event.id}" class="btn btn-sm btn-secondary btn-block">
                                Cancel Registration
                            </a>
                        </div>
                    </c:when>
                    <c:when test="${sessionScope.userRole eq 'STUDENT'}">
                        <c:choose>
                            <c:when test="${event.availability eq 'OPEN'}">
                                <p class="cta-notice">
                                    Reserve your seat now. Registration is free for college students.
                                </p>
                                <form action="${pageContext.request.contextPath}/student/events/register" method="POST">
                                    <input type="hidden" name="_csrf" value="${csrfToken}"/>
                                    <input type="hidden" name="eventId" value="${event.id}"/>
                                    <button type="submit" class="btn btn-primary btn-block">
                                        Register for Event
                                    </button>
                                </form>
                            </c:when>
                            <c:otherwise>
                                <p class="cta-notice">
                                    Registrations are currently unavailable for this event.
                                </p>
                                <button type="button" class="btn btn-secondary btn-block" disabled>
                                    <c:out value="${event.availabilityLabel}"/>
                                </button>
                            </c:otherwise>
                        </c:choose>
                    </c:when>
                    <c:when test="${sessionScope.userRole eq 'ADMIN'}">
                        <div class="admin-notice-inline">
                            <p class="cta-notice">Logged in as Administrator (student registration only).</p>
                            <a href="${pageContext.request.contextPath}/admin/events" class="btn btn-secondary btn-block">
                                Manage Events
                            </a>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <p class="cta-notice">
                            Sign in with your student account to register for this event.
                        </p>
                        <a href="${pageContext.request.contextPath}/login" class="btn btn-primary btn-block">
                            Sign In with Student Account
                        </a>
                    </c:otherwise>
                </c:choose>
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
