<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="pageTitle" value="System Health - CampusSphere" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<section class="hero-card">
    <h1 class="hero-title">System Health Diagnostic</h1>
    <p class="hero-tagline">Tomcat Runtime &amp; Database Connectivity</p>

    <div class="health-status-box">
        <c:choose>
            <c:when test="${dbStatus eq 'CONFIGURED_OK'}">
                <div class="alert alert-success">
                    <strong>Status: OK</strong>
                    <p>Web container is running and database connection is operational.</p>
                </div>
            </c:when>
            <c:when test="${dbStatus eq 'NOT_CONFIGURED'}">
                <div class="alert alert-warning">
                    <strong>Status: Database not configured yet</strong>
                    <p>Application is operational. Database credentials have not been configured yet in <code>~/.campussphere/campussphere.properties</code>.</p>
                </div>
            </c:when>
            <c:otherwise>
                <div class="alert alert-danger">
                    <strong>Status: Database unreachable</strong>
                    <p>Database credentials are set, but connection to MySQL server could not be established. Ensure MySQL service is running on the configured port.</p>
                </div>
            </c:otherwise>
        </c:choose>

        <p class="raw-status">Diagnostic Code: <code><c:out value="${dbStatus}" /></code></p>
    </div>

    <div class="action-buttons">
        <a href="${pageContext.request.contextPath}/" class="btn btn-secondary">&larr; Return to Home</a>
    </div>
</section>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
