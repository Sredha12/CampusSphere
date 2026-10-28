<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<nav class="admin-subnav" aria-label="Administrator Navigation">
    <div class="admin-subnav-links">
        <a href="${pageContext.request.contextPath}/admin/dashboard" 
           class="admin-subnav-link ${adminActiveNav eq 'dashboard' ? 'active' : ''}"
           ${adminActiveNav eq 'dashboard' ? 'aria-current="page"' : ''}>
            <span aria-hidden="true">📊</span> Dashboard
        </a>
        <a href="${pageContext.request.contextPath}/admin/clubs" 
           class="admin-subnav-link ${adminActiveNav eq 'clubs' ? 'active' : ''}"
           ${adminActiveNav eq 'clubs' ? 'aria-current="page"' : ''}>
            <span aria-hidden="true">🏛️</span> Manage Clubs
        </a>
        <a href="${pageContext.request.contextPath}/admin/clubs/new" 
           class="admin-subnav-link ${adminActiveNav eq 'clubs-new' ? 'active' : ''}"
           ${adminActiveNav eq 'clubs-new' ? 'aria-current="page"' : ''}>
            <span aria-hidden="true">➕</span> Add Club
        </a>
        <a href="${pageContext.request.contextPath}/admin/events" 
           class="admin-subnav-link ${adminActiveNav eq 'events' ? 'active' : ''}"
           ${adminActiveNav eq 'events' ? 'aria-current="page"' : ''}>
            <span aria-hidden="true">📅</span> Manage Events
        </a>
        <a href="${pageContext.request.contextPath}/admin/events/new" 
           class="admin-subnav-link ${adminActiveNav eq 'events-new' ? 'active' : ''}"
           ${adminActiveNav eq 'events-new' ? 'aria-current="page"' : ''}>
            <span aria-hidden="true">➕</span> Add Event
        </a>
        <a href="${pageContext.request.contextPath}/admin/registrations" 
           class="admin-subnav-link ${adminActiveNav eq 'registrations' ? 'active' : ''}"
           ${adminActiveNav eq 'registrations' ? 'aria-current="page"' : ''}>
            <span aria-hidden="true">📝</span> Registrations
        </a>
    </div>
</nav>
