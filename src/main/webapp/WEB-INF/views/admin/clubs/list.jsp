<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="admin-page-container">
    <jsp:include page="/WEB-INF/views/admin/common/admin-nav.jsp"/>

    <div class="admin-header-row">
        <div>
            <div class="breadcrumbs">
                <a href="${pageContext.request.contextPath}/admin/dashboard">Admin</a> &rsaquo; <span>Clubs</span>
            </div>
            <h1 class="page-title">Manage Student Clubs</h1>
            <p class="page-subtitle">Oversee registered student societies, club descriptions, and contact channels.</p>
        </div>
        <div class="admin-header-actions">
            <a href="${pageContext.request.contextPath}/admin/clubs/new" class="btn btn-primary">
                <span aria-hidden="true">➕</span> Add Club
            </a>
        </div>
    </div>

    <c:choose>
        <c:when test="${not empty clubs}">
            <div class="admin-table-card">
                <div class="table-responsive">
                    <table class="admin-table">
                        <thead>
                            <tr>
                                <th scope="col">Club Name</th>
                                <th scope="col">Contact Email</th>
                                <th scope="col" class="text-center">Total Events</th>
                                <th scope="col" class="text-right">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${clubs}" var="club">
                                <tr>
                                    <td>
                                        <div class="club-table-info">
                                            <strong><c:out value="${club.name}"/></strong>
                                            <c:if test="${not empty club.description}">
                                                <small class="text-muted line-clamp-1"><c:out value="${club.description}"/></small>
                                            </c:if>
                                        </div>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty club.contactEmail}">
                                                <a href="mailto:<c:out value="${club.contactEmail}"/>" class="table-email-link">
                                                    <c:out value="${club.contactEmail}"/>
                                                </a>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-muted">&mdash;</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="text-center">
                                        <span class="badge badge-primary-subtle">
                                            <c:out value="${club.totalEventCount}"/>
                                        </span>
                                    </td>
                                    <td class="text-right">
                                        <div class="action-btn-group">
                                            <a href="${pageContext.request.contextPath}/admin/clubs/edit?id=${club.id}" 
                                               class="btn btn-sm btn-secondary" 
                                               title="Edit Club <c:out value="${club.name}"/>">
                                                Edit
                                            </a>
                                            <a href="${pageContext.request.contextPath}/admin/clubs/delete-confirm?id=${club.id}" 
                                               class="btn btn-sm btn-danger" 
                                               title="Delete Club <c:out value="${club.name}"/>">
                                                Delete
                                            </a>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </c:when>
        <c:otherwise>
            <div class="empty-state">
                <div class="empty-icon" aria-hidden="true">🏛️</div>
                <h3>No Clubs Registered</h3>
                <p>There are currently no student clubs or societies registered in the system.</p>
                <a href="${pageContext.request.contextPath}/admin/clubs/new" class="btn btn-primary">Create the First Club</a>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
