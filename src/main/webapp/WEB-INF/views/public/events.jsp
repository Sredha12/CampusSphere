<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="page-header">
    <div class="breadcrumbs">
        <a href="${pageContext.request.contextPath}/">Home</a> &rsaquo; <span>Events</span>
    </div>
    <h1 class="page-title">Campus Events Catalog</h1>
    <p class="page-subtitle">Explore, filter, and discover all upcoming events organized across campus</p>
</div>

<!-- Search & Filter Form -->
<section class="filter-section">
    <form action="${pageContext.request.contextPath}/events" method="GET" class="filter-form" role="search">
        <div class="filter-group">
            <label for="searchQuery" class="filter-label">Search Keywords</label>
            <input type="text" 
                   id="searchQuery" 
                   name="q" 
                   value="<c:out value="${searchQuery}"/>" 
                   placeholder="Search by title, club, venue..." 
                   maxlength="100" 
                   class="form-control">
        </div>

        <div class="filter-group">
            <label for="categorySelect" class="filter-label">Category</label>
            <select id="categorySelect" name="category" class="form-control">
                <option value="">All Categories</option>
                <c:forEach items="${categories}" var="cat">
                    <option value="<c:out value="${cat}"/>" ${selectedCategory eq cat ? 'selected' : ''}>
                        <c:out value="${cat}"/>
                    </option>
                </c:forEach>
            </select>
        </div>

        <div class="filter-actions">
            <button type="submit" class="btn btn-primary">Filter Events</button>
            <c:if test="${not empty selectedCategory or not empty searchQuery}">
                <a href="${pageContext.request.contextPath}/events" class="btn btn-ghost">Clear Filters</a>
            </c:if>
        </div>
    </form>
</section>

<!-- Events Results -->
<section class="section">
    <div class="results-meta">
        <span class="results-count">
            Showing <strong><c:out value="${fn:length(events)}"/></strong> upcoming 
            <c:choose>
                <c:when test="${fn:length(events) eq 1}">event</c:when>
                <c:otherwise>events</c:otherwise>
            </c:choose>
            <c:if test="${not empty selectedCategory}">
                in <em><c:out value="${selectedCategory}"/></em>
            </c:if>
            <c:if test="${not empty searchQuery}">
                matching "<em><c:out value="${searchQuery}"/></em>"
            </c:if>
        </span>
    </div>

    <c:choose>
        <c:when test="${not empty events}">
            <div class="cards-grid">
                <c:forEach items="${events}" var="event">
                    <c:set var="event" value="${event}" scope="request"/>
                    <jsp:include page="/WEB-INF/views/common/event-card.jsp"/>
                </c:forEach>
            </div>
        </c:when>
        <c:otherwise>
            <div class="empty-state">
                <div class="empty-icon" aria-hidden="true">🔍</div>
                <h3>No Matching Events Found</h3>
                <p>
                    <c:choose>
                        <c:when test="${not empty selectedCategory or not empty searchQuery}">
                            We couldn't find any upcoming events matching your filter criteria. Try adjusting your search keywords or clearing filters.
                        </c:when>
                        <c:otherwise>
                            There are currently no upcoming events registered in the system. Check back soon!
                        </c:otherwise>
                    </c:choose>
                </p>
                <c:if test="${not empty selectedCategory or not empty searchQuery}">
                    <a href="${pageContext.request.contextPath}/events" class="btn btn-primary">View All Events</a>
                </c:if>
            </div>
        </c:otherwise>
    </c:choose>
</section>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
