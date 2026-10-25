<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="page-header">
    <div class="breadcrumbs">
        <a href="${pageContext.request.contextPath}/">Home</a> &rsaquo; <span>Clubs</span>
    </div>
    <h1 class="page-title">Student Clubs &amp; Societies</h1>
    <p class="page-subtitle">Connect with technical, cultural, sports, and academic societies driving campus life</p>
</div>

<section class="section">
    <c:choose>
        <c:when test="${not empty clubs}">
            <div class="cards-grid clubs-grid">
                <c:forEach items="${clubs}" var="club">
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
                                    <c:when test="${fn:length(club.description) > 140}">
                                        <c:out value="${fn:substring(club.description, 0, 140)}"/>...
                                    </c:when>
                                    <c:otherwise>
                                        <c:out value="${club.description}"/>
                                    </c:otherwise>
                                </c:choose>
                            </p>
                            <div class="card-footer">
                                <span class="club-contact"><c:out value="${club.contactEmail}"/></span>
                                <a href="${pageContext.request.contextPath}/clubs/detail?id=${club.id}" class="btn btn-sm btn-secondary">
                                    View Profile
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
                <p>There are currently no clubs registered in the system. Check back soon!</p>
                <a href="${pageContext.request.contextPath}/" class="btn btn-primary">Return to Homepage</a>
            </div>
        </c:otherwise>
    </c:choose>
</section>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
