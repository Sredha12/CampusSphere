<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<section class="hero-section">
    <div class="hero-content">
        <span class="badge badge-pill badge-primary-subtle">College Event Management</span>
        <h1 class="hero-heading">Every Event. One Campus. One Platform.</h1>
        <p class="hero-subtext">
            Discover upcoming workshops, hackathons, seminars, and cultural festivals hosted by student clubs across the campus.
        </p>
        <div class="hero-actions">
            <a href="${pageContext.request.contextPath}/events" class="btn btn-primary btn-lg">Explore Events</a>
            <a href="${pageContext.request.contextPath}/clubs" class="btn btn-secondary btn-lg">Browse Clubs</a>
        </div>
    </div>
</section>

<!-- Upcoming Events Section -->
<section class="section">
    <div class="section-header">
        <div>
            <h2 class="section-title">Upcoming Campus Events</h2>
            <p class="section-subtitle">Reserve your seat for upcoming activities happening around the campus</p>
        </div>
        <a href="${pageContext.request.contextPath}/events" class="link-arrow">View all events &rarr;</a>
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
                <h3>No Upcoming Events</h3>
                <p>There are no upcoming events scheduled at the moment. Explore our clubs or check back soon!</p>
                <a href="${pageContext.request.contextPath}/clubs" class="btn btn-secondary">Browse Student Clubs</a>
            </div>
        </c:otherwise>
    </c:choose>
</section>

<!-- Featured Clubs Section -->
<section class="section section-alt">
    <div class="section-header">
        <div>
            <h2 class="section-title">Student Clubs &amp; Societies</h2>
            <p class="section-subtitle">Active student organizations organizing campus life and tech innovation</p>
        </div>
        <a href="${pageContext.request.contextPath}/clubs" class="link-arrow">View all clubs &rarr;</a>
    </div>

    <c:choose>
        <c:when test="${not empty featuredClubs}">
            <div class="cards-grid clubs-grid">
                <c:forEach items="${featuredClubs}" var="club">
                    <article class="card club-card">
                        <div class="card-body">
                            <div class="club-header">
                                <div class="club-avatar" aria-hidden="true">
                                    <c:out value="${fn:substring(club.name, 0, 1)}"/>
                                </div>
                                <div>
                                    <h3 class="club-name">
                                        <a href="${pageContext.request.contextPath}/clubs/detail?id=${club.id}" class="card-link">
                                            <c:out value="${club.name}"/>
                                        </a>
                                    </h3>
                                    <span class="club-meta"><c:out value="${club.upcomingEventCount}"/> upcoming events</span>
                                </div>
                            </div>
                            <p class="club-description">
                                <c:choose>
                                    <c:when test="${fn:length(club.description) > 120}">
                                        <c:out value="${fn:substring(club.description, 0, 120)}"/>...
                                    </c:when>
                                    <c:otherwise>
                                        <c:out value="${club.description}"/>
                                    </c:otherwise>
                                </c:choose>
                            </p>
                            <div class="card-footer">
                                <span class="club-contact"><c:out value="${club.contactEmail}"/></span>
                                <a href="${pageContext.request.contextPath}/clubs/detail?id=${club.id}" class="btn btn-sm btn-secondary">
                                    View Club
                                </a>
                            </div>
                        </div>
                    </article>
                </c:forEach>
            </div>
        </c:when>
        <c:otherwise>
            <div class="empty-state">
                <div class="empty-icon" aria-hidden="true">🏛️</div>
                <h3>No Clubs Registered Yet</h3>
                <p>Student clubs are being set up. Check back soon!</p>
            </div>
        </c:otherwise>
    </c:choose>
</section>

<!-- How It Works Section -->
<section class="section">
    <div class="section-header text-center">
        <h2 class="section-title">How CampusSphere Works</h2>
        <p class="section-subtitle">A seamless journey from event discovery to participation</p>
    </div>

    <div class="steps-grid">
        <div class="step-card">
            <div class="step-number" aria-hidden="true">1</div>
            <h3>Discover Events</h3>
            <p>Browse campus workshops, tech hackathons, cultural festivals, and seminars filtered by category.</p>
        </div>
        <div class="step-card">
            <div class="step-number" aria-hidden="true">2</div>
            <h3>Instant Booking</h3>
            <p>Register with your student ID before registration deadlines, backed by real-time seat availability.</p>
        </div>
        <div class="step-card">
            <div class="step-number" aria-hidden="true">3</div>
            <h3>Track Activity</h3>
            <p>Access your personal event history, verify registrations, and stay informed on upcoming schedules.</p>
        </div>
    </div>
</section>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
