<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="pageTitle" value="Page Not Found (404) - CampusSphere" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<section class="hero-card error-card">
    <div class="error-code">404</div>
    <h1 class="hero-title">Page Not Found</h1>
    <p class="hero-tagline">The resource you requested could not be located on this server.</p>
    <p class="hero-description">
        The link you followed may be incorrect, or the event or club may have been removed.
    </p>
    <div class="action-buttons">
        <a href="${pageContext.request.contextPath}/" class="btn btn-primary">&larr; Return to Home</a>
        <a href="${pageContext.request.contextPath}/events" class="btn btn-secondary">Browse Events</a>
    </div>
</section>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
