<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>CampusSphere - System Health</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>
    <header class="app-header">
        <div class="container header-container">
            <div class="logo">
                <span class="logo-accent">Campus</span>Sphere
            </div>
            <nav class="nav-links">
                <a href="${pageContext.request.contextPath}/" class="nav-link">Home</a>
                <a href="${pageContext.request.contextPath}/health" class="nav-link active">System Health</a>
            </nav>
        </div>
    </header>

    <main class="container main-content">
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
    </main>

    <footer class="app-footer">
        <div class="container footer-container">
            <p>&copy; CampusSphere - College Event Management System</p>
        </div>
    </footer>
</body>
</html>
