<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="auth-page-wrapper">
    <div class="auth-card admin-auth-card">
        <div class="auth-header">
            <span class="auth-icon" aria-hidden="true">🛡️</span>
            <h1 class="auth-title">Administrator Portal</h1>
            <p class="auth-subtitle">Authorized campus administration and event oversight access.</p>
        </div>

        <c:if test="${not empty loginError}">
            <div class="alert alert-error auth-alert" role="alert" aria-live="assertive">
                <span class="alert-icon" aria-hidden="true">⚠</span>
                <div class="alert-text"><c:out value="${loginError}"/></div>
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/admin/login" method="post" class="auth-form" novalidate>
            <input type="hidden" name="_csrf" value="${csrfToken}">

            <div class="form-group">
                <label for="email" class="form-label">Admin Email <span class="required-indicator" aria-hidden="true">*</span></label>
                <input type="email" 
                       id="email" 
                       name="email" 
                       class="form-control ${not empty loginError ? 'is-invalid' : ''}"
                       value="<c:out value="${email}"/>" 
                       required 
                       maxlength="150" 
                       autocomplete="email"
                       autofocus
                       aria-required="true"
                       placeholder="admin@college.edu">
            </div>

            <div class="form-group">
                <label for="password" class="form-label">Password <span class="required-indicator" aria-hidden="true">*</span></label>
                <input type="password" 
                       id="password" 
                       name="password" 
                       class="form-control ${not empty loginError ? 'is-invalid' : ''}"
                       required 
                       autocomplete="current-password"
                       aria-required="true"
                       placeholder="Enter administrator password">
            </div>

            <button type="submit" class="btn btn-primary btn-block btn-lg auth-submit-btn">
                Log In to Admin Portal
            </button>
        </form>

        <div class="auth-footer-links">
            <p>Student looking to sign in? <a href="${pageContext.request.contextPath}/login">Go to Student Login</a></p>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
