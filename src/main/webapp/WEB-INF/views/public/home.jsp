<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>CampusSphere - College Event Management System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>
    <header class="app-header">
        <div class="container header-container">
            <div class="logo">
                <span class="logo-accent">Campus</span>Sphere
            </div>
            <nav class="nav-links">
                <a href="${pageContext.request.contextPath}/" class="nav-link active">Home</a>
                <a href="${pageContext.request.contextPath}/health" class="nav-link">System Health</a>
            </nav>
        </div>
    </header>

    <main class="container main-content">
        <section class="hero-card">
            <div class="badge-status">
                <c:if test="${not empty foundationStatus}">
                    <span class="status-indicator"></span> Foundation <c:out value="${foundationStatus}" />
                </c:if>
            </div>
            <h1 class="hero-title"><c:out value="${systemName}" /></h1>
            <p class="hero-tagline"><c:out value="${welcomeMessage}" /></p>
            <p class="hero-description">
                The Maven and Jakarta EE 10 application foundation is successfully initialized and running on Apache Tomcat 10.1.
            </p>
            <div class="action-buttons">
                <a href="${pageContext.request.contextPath}/health" class="btn btn-primary">Check System Health</a>
            </div>
        </section>

        <section class="info-grid">
            <div class="info-card">
                <h3>Architecture Ready</h3>
                <p>3-Tier separation configured with Servlets, JSTL 3.0 views, and JDBC utilities.</p>
            </div>
            <div class="info-card">
                <h3>Jakarta EE 10 Standard</h3>
                <p>Running with Jakarta Servlet 6.0 and JSP 3.1 specifications under Java 21 LTS.</p>
            </div>
            <div class="info-card">
                <h3>Secure External Configuration</h3>
                <p>Database credentials isolated outside repository source files.</p>
            </div>
        </section>
    </main>

    <footer class="app-footer">
        <div class="container footer-container">
            <p>&copy; CampusSphere - College Event Management System. Built with Java 21 &amp; Tomcat 10.1.</p>
        </div>
    </footer>
</body>
</html>
