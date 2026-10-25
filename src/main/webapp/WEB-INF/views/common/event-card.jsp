<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<article class="card event-card">
    <div class="card-media category-media-${fn:toLowerCase(event.category)}">
        <c:choose>
            <c:when test="${not empty event.safeImageUrl}">
                <img src="${pageContext.request.contextPath}${event.safeImageUrl}" alt="<c:out value="${event.title}"/>" class="card-img" loading="lazy">
            </c:when>
            <c:otherwise>
                <div class="category-banner" aria-hidden="true">
                    <span class="category-letter"><c:out value="${fn:substring(event.category, 0, 1)}"/></span>
                </div>
            </c:otherwise>
        </c:choose>
        <div class="card-badges">
            <span class="badge badge-category"><c:out value="${event.category}"/></span>
            <span class="badge badge-status badge-${fn:toLowerCase(event.availability)}">
                <span class="status-dot status-dot-${fn:toLowerCase(event.availability)}" aria-hidden="true"></span>
                <c:out value="${event.availabilityLabel}"/>
            </span>
        </div>
    </div>

    <div class="card-body">
        <div class="event-meta">
            <span class="event-club">
                <c:out value="${event.clubName}"/>
            </span>
        </div>

        <h3 class="card-title">
            <a href="${pageContext.request.contextPath}/events/detail?id=${event.id}" class="card-link">
                <c:out value="${event.title}"/>
            </a>
        </h3>

        <div class="event-details-list">
            <div class="detail-item">
                <span class="detail-icon" aria-hidden="true">📅</span>
                <span class="detail-text"><c:out value="${event.eventDate}"/></span>
            </div>
            <div class="detail-item">
                <span class="detail-icon" aria-hidden="true">⏰</span>
                <span class="detail-text"><c:out value="${event.startTime}"/> - <c:out value="${event.endTime}"/></span>
            </div>
            <div class="detail-item">
                <span class="detail-icon" aria-hidden="true">📍</span>
                <span class="detail-text"><c:out value="${event.venue}"/></span>
            </div>
        </div>

        <div class="card-footer">
            <div class="capacity-info">
                <c:choose>
                    <c:when test="${event.capacity ne null}">
                        <span class="capacity-text">
                            <strong><c:out value="${event.seatsLeft}"/></strong> of <c:out value="${event.capacity}"/> seats left
                        </span>
                    </c:when>
                    <c:otherwise>
                        <span class="capacity-text">Unlimited capacity</span>
                    </c:otherwise>
                </c:choose>
            </div>
            <a href="${pageContext.request.contextPath}/events/detail?id=${event.id}" class="btn btn-sm btn-secondary">
                View Details
            </a>
        </div>
    </div>
</article>
