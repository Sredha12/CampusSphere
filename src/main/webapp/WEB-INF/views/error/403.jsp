<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="pageTitle" value="Access Forbidden (403) - CampusSphere" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<section class="hero-card error-card">
    <div class="error-code">403</div>
    <h1 class="hero-title">Access Forbidden</h1>
    <p class="hero-tagline">You do not have permission to access the requested resource or form submission.</p>
    <p class="hero-description">
        This may occur if your session expired or if a security token could not be verified. Please return to the homepage or log in again.
    </p>
    <div class="action-buttons">
        <a href="${pageContext.request.contextPath}/" class="btn btn-primary">&larr; Return to Home</a>
        <a href="${pageContext.request.contextPath}/login" class="btn btn-secondary">Student Login</a>
    </div>
</section>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
