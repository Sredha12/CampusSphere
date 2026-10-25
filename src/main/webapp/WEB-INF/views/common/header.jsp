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
                <a href="${pageContext.request.contextPath}/login" 
                   class="nav-link nav-link-auth ${activeNav eq 'login' ? 'active' : ''}" 
                   ${activeNav eq 'login' ? 'aria-current="page"' : ''}>Login</a>
                <a href="${pageContext.request.contextPath}/register" 
                   class="btn btn-sm btn-primary ${activeNav eq 'register' ? 'active' : ''}" 
                   ${activeNav eq 'register' ? 'aria-current="page"' : ''}>Register</a>
            </nav>
        </div>
    </header>

    <main id="main-content" class="main-content container">
