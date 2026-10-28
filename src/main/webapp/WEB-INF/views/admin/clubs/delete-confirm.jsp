<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="admin-page-container">
    <jsp:include page="/WEB-INF/views/admin/common/admin-nav.jsp"/>

    <div class="admin-confirm-card">
        <div class="confirm-card-header">
            <div class="breadcrumbs">
                <a href="${pageContext.request.contextPath}/admin/dashboard">Admin</a> &rsaquo; 
                <a href="${pageContext.request.contextPath}/admin/clubs">Clubs</a> &rsaquo; 
                <span>Delete Confirmation</span>
            </div>
            <h1 class="confirm-card-title">Delete Club Confirmation</h1>
        </div>

        <div class="confirm-details-box">
            <h2 class="confirm-entity-name"><c:out value="${club.name}"/></h2>
            <c:if test="${not empty club.contactEmail}">
                <p class="confirm-meta"><strong>Contact Email:</strong> <c:out value="${club.contactEmail}"/></p>
            </c:if>
            <p class="confirm-meta"><strong>Associated Events:</strong> <c:out value="${eventCount}"/></p>
        </div>

        <c:choose>
            <c:when test="${eventCount > 0}">
                <div class="alert alert-error" role="alert">
                    <span class="alert-icon" aria-hidden="true">🚫</span>
                    <div class="alert-text">
                        <strong>Deletion Blocked:</strong> This club has <strong><c:out value="${eventCount}"/></strong> event(s) associated with it. Delete or move those events first before deleting this club.
                    </div>
                </div>

                <div class="confirm-actions">
                    <a href="${pageContext.request.contextPath}/admin/events" class="btn btn-primary">
                        Review Associated Events
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/clubs" class="btn btn-secondary">
                        Back to Clubs
                    </a>
                </div>
            </c:when>

            <c:otherwise>
                <div class="alert alert-warning" role="alert">
                    <span class="alert-icon" aria-hidden="true">⚠</span>
                    <div class="alert-text">
                        <strong>Warning:</strong> Are you sure you want to permanently delete this club? This action cannot be undone.
                    </div>
                </div>

                <form action="${pageContext.request.contextPath}/admin/clubs/delete" method="post" class="confirm-form">
                    <input type="hidden" name="_csrf" value="${csrfToken}">
                    <input type="hidden" name="id" value="${club.id}">

                    <div class="confirm-actions">
                        <button type="submit" class="btn btn-danger">
                            Permanently Delete Club
                        </button>
                        <a href="${pageContext.request.contextPath}/admin/clubs" class="btn btn-secondary">
                            Cancel
                        </a>
                    </div>
                </form>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
