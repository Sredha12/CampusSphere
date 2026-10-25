<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>CampusSphere - System Error (500)</title>
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
            </nav>
        </div>
    </header>

    <main class="container main-content">
        <section class="hero-card error-card">
            <div class="error-code">500</div>
            <h1 class="hero-title">Internal Server Error</h1>
            <p class="hero-tagline">An unexpected error occurred while processing your request.</p>
            <p class="hero-description">
                Our application encountered an internal issue. Details have been safely logged on the server.
            </p>
            <div class="action-buttons">
                <a href="${pageContext.request.contextPath}/" class="btn btn-primary">&larr; Return to Home</a>
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
