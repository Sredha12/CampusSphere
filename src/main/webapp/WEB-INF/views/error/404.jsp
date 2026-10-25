<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>CampusSphere - Page Not Found (404)</title>
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
            <div class="error-code">404</div>
            <h1 class="hero-title">Page Not Found</h1>
            <p class="hero-tagline">The resource you requested could not be located on this server.</p>
            <p class="hero-description">
                Please verify the URL or return to the main dashboard.
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
