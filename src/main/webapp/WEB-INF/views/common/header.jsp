<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:out value="${not empty pageTitle ? pageTitle : 'CampusSphere - College Event Management System'}" /></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>
    <a href="#main-content" class="skip-link">Skip to main content</a>

    <header class="app-header">
        <div class="container header-container">
            <a href="${pageContext.request.contextPath}/" class="logo" aria-label="CampusSphere Home">
                <span class="logo-accent">Campus</span>Sphere
            </a>

            <button class="mobile-menu-btn" id="menuToggle" type="button" aria-expanded="false" aria-controls="primaryNav" aria-label="Toggle navigation menu">
                <span class="menu-icon" aria-hidden="true"></span>
            </button>

            <nav id="primaryNav" class="nav-links" aria-label="Main Navigation">
                <a href="${pageContext.request.contextPath}/" 
                   class="nav-link ${activeNav eq 'home' ? 'active' : ''}" 
                   ${activeNav eq 'home' ? 'aria-current="page"' : ''}>Home</a>
                <a href="${pageContext.request.contextPath}/events" 
                   class="nav-link ${activeNav eq 'events' ? 'active' : ''}" 
                   ${activeNav eq 'events' ? 'aria-current="page"' : ''}>Events</a>
                <a href="${pageContext.request.contextPath}/clubs" 
                   class="nav-link ${activeNav eq 'clubs' ? 'active' : ''}" 
                   ${activeNav eq 'clubs' ? 'aria-current="page"' : ''}>Clubs</a>

                <span class="nav-divider" aria-hidden="true"></span>

                <c:choose>
                    <c:when test="${sessionScope.userRole eq 'STUDENT'}">
                        <a href="${pageContext.request.contextPath}/student/dashboard" 
                           class="nav-link ${activeNav eq 'dashboard' ? 'active' : ''}" 
                           ${activeNav eq 'dashboard' ? 'aria-current="page"' : ''}>Dashboard</a>
                        <a href="${pageContext.request.contextPath}/student/profile" 
                           class="nav-link ${activeNav eq 'profile' ? 'active' : ''}" 
                           ${activeNav eq 'profile' ? 'aria-current="page"' : ''}>Profile</a>
                        <span class="nav-user-greeting" aria-label="Signed in as <c:out value="${sessionScope.userName}"/>">
                            <span class="user-avatar-badge" aria-hidden="true">👤</span>
                            <span class="user-name"><c:out value="${sessionScope.userName}"/></span>
                        </span>
                        <form action="${pageContext.request.contextPath}/logout" method="post" class="nav-logout-form">
                            <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">
                            <button type="submit" class="btn btn-sm btn-ghost nav-logout-btn">Log out</button>
                        </form>
                    </c:when>

                    <c:when test="${sessionScope.userRole eq 'ADMIN'}">
                        <a href="${pageContext.request.contextPath}/admin/dashboard" 
                           class="nav-link ${activeNav eq 'adminDashboard' ? 'active' : ''}" 
                           ${activeNav eq 'adminDashboard' ? 'aria-current="page"' : ''}>Dashboard</a>
                        <a href="${pageContext.request.contextPath}/admin/clubs" 
                           class="nav-link ${adminActiveNav eq 'clubs' || adminActiveNav eq 'clubs-new' ? 'active' : ''}">Clubs</a>
                        <a href="${pageContext.request.contextPath}/admin/events" 
                           class="nav-link ${adminActiveNav eq 'events' || adminActiveNav eq 'events-new' ? 'active' : ''}">Events</a>
                        <span class="nav-user-greeting" aria-label="Signed in as Administrator <c:out value="${sessionScope.userName}"/>">
                            <span class="user-avatar-badge admin-avatar-badge" aria-hidden="true">🛡️</span>
                            <span class="user-name"><c:out value="${sessionScope.userName}"/> <small class="role-badge">(Admin)</small></span>
                        </span>
                        <form action="${pageContext.request.contextPath}/logout" method="post" class="nav-logout-form">
                            <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">
                            <button type="submit" class="btn btn-sm btn-ghost nav-logout-btn">Log out</button>
                        </form>
                    </c:when>

                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/login" 
                           class="nav-link nav-link-auth ${activeNav eq 'login' ? 'active' : ''}" 
                           ${activeNav eq 'login' ? 'aria-current="page"' : ''}>Login</a>
                        <a href="${pageContext.request.contextPath}/register" 
                           class="btn btn-sm btn-primary ${activeNav eq 'register' ? 'active' : ''}" 
                           ${activeNav eq 'register' ? 'aria-current="page"' : ''}>Register</a>
                    </c:otherwise>
                </c:choose>
            </nav>
        </div>
    </header>

    <main id="main-content" class="main-content container">

        <%-- Flash Alert Notifications (Accessible status/alert live regions) --%>
        <c:if test="${not empty sessionScope.flashSuccess}">
            <div class="alert alert-success" role="status" aria-live="polite">
                <span class="alert-icon" aria-hidden="true">✓</span>
                <div class="alert-text"><c:out value="${sessionScope.flashSuccess}"/></div>
            </div>
            <c:remove var="flashSuccess" scope="session"/>
        </c:if>

        <c:if test="${not empty sessionScope.flashError}">
            <div class="alert alert-error" role="alert" aria-live="assertive">
                <span class="alert-icon" aria-hidden="true">⚠</span>
                <div class="alert-text"><c:out value="${sessionScope.flashError}"/></div>
            </div>
            <c:remove var="flashError" scope="session"/>
        </c:if>
