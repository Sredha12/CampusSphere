<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="pageTitle" value="System Error (500) - CampusSphere" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<section class="hero-card error-card">
    <div class="error-code">500</div>
    <h1 class="hero-title">Internal Server Error</h1>
    <p class="hero-tagline">An unexpected condition occurred while processing your request.</p>
    <p class="hero-description">
        Our technical team has been notified through server error logs. No confidential details are exposed.
    </p>
    <div class="action-buttons">
        <a href="${pageContext.request.contextPath}/" class="btn btn-primary">&larr; Return to Home</a>
    </div>
</section>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
