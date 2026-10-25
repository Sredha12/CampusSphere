<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="coming-soon-card">
    <div class="coming-soon-icon" aria-hidden="true">🔒</div>
    <span class="badge badge-pill badge-primary-subtle">Phase 5 Feature</span>
    <h1 class="coming-soon-title"><c:out value="${authAction}"/></h1>
    <p class="coming-soon-subtitle">Student authentication arrives in the next build phase.</p>
    <div class="coming-soon-box">
        <p>
            Secure password hashing (PBKDF2), role-based session management, and self-service registration are scheduled for implementation in <strong>Phase 5: Authentication &amp; Access Control</strong>.
        </p>
        <p>
            In this build phase, all event listings, club directories, and campus activity catalogs are publicly accessible without signing in.
        </p>
    </div>
    <div class="action-buttons">
        <a href="${pageContext.request.contextPath}/" class="btn btn-primary">Return to Homepage</a>
        <a href="${pageContext.request.contextPath}/events" class="btn btn-secondary">Explore Events</a>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
