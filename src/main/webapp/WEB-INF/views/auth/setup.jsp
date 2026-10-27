<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="auth-page-wrapper">
    <div class="auth-card admin-auth-card">
        <div class="auth-header">
            <span class="auth-icon" aria-hidden="true">⚙️</span>
            <h1 class="auth-title">System Bootstrap Setup</h1>
            <p class="auth-subtitle">One-time initialization to create the primary administrator account.</p>
        </div>

        <c:if test="${not empty genericError}">
            <div class="alert alert-error auth-alert" role="alert" aria-live="assertive">
                <span class="alert-icon" aria-hidden="true">⚠</span>
                <div class="alert-text"><c:out value="${genericError}"/></div>
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/setup" method="post" class="auth-form" novalidate>
            <input type="hidden" name="_csrf" value="${csrfToken}">

            <div class="form-group">
                <label for="setupKey" class="form-label">Setup Authorization Key <span class="required-indicator" aria-hidden="true">*</span></label>
                <input type="password" 
                       id="setupKey" 
                       name="setupKey" 
                       class="form-control ${not empty errors['setupKey'] ? 'is-invalid' : ''}"
                       required 
                       autocomplete="off"
                       aria-required="true"
                       <c:if test="${not empty errors['setupKey']}">aria-invalid="true" aria-describedby="setupKey-error"</c:if>
                       placeholder="Enter configured setup key">
                <span class="field-help">Configured in application environment or properties file</span>
                <c:if test="${not empty errors['setupKey']}">
                    <span id="setupKey-error" class="field-error" role="alert"><c:out value="${errors['setupKey']}"/></span>
                </c:if>
            </div>

            <div class="form-group">
                <label for="fullName" class="form-label">Administrator Full Name <span class="required-indicator" aria-hidden="true">*</span></label>
                <input type="text" 
                       id="fullName" 
                       name="fullName" 
                       class="form-control ${not empty errors['fullName'] ? 'is-invalid' : ''}"
                       value="<c:out value="${fullName}"/>" 
                       required 
                       minlength="2" 
                       maxlength="100" 
                       autocomplete="name"
                       aria-required="true"
                       <c:if test="${not empty errors['fullName']}">aria-invalid="true" aria-describedby="fullName-error"</c:if>
                       placeholder="e.g. System Administrator">
                <c:if test="${not empty errors['fullName']}">
                    <span id="fullName-error" class="field-error" role="alert"><c:out value="${errors['fullName']}"/></span>
                </c:if>
            </div>

            <div class="form-group">
                <label for="email" class="form-label">Administrator Email <span class="required-indicator" aria-hidden="true">*</span></label>
                <input type="email" 
                       id="email" 
                       name="email" 
                       class="form-control ${not empty errors['email'] ? 'is-invalid' : ''}"
                       value="<c:out value="${email}"/>" 
                       required 
                       maxlength="150" 
                       autocomplete="email"
                       aria-required="true"
                       <c:if test="${not empty errors['email']}">aria-invalid="true" aria-describedby="email-error"</c:if>
                       placeholder="e.g. admin@college.edu">
                <c:if test="${not empty errors['email']}">
                    <span id="email-error" class="field-error" role="alert"><c:out value="${errors['email']}"/></span>
                </c:if>
            </div>

            <div class="form-row">
                <div class="form-group form-col">
                    <label for="password" class="form-label">Password <span class="required-indicator" aria-hidden="true">*</span></label>
                    <input type="password" 
                           id="password" 
                           name="password" 
                           class="form-control ${not empty errors['password'] ? 'is-invalid' : ''}"
                           required 
                           minlength="8" 
                           maxlength="128" 
                           autocomplete="new-password"
                           aria-required="true"
                           <c:if test="${not empty errors['password']}">aria-invalid="true" aria-describedby="password-error"</c:if>
                           placeholder="8+ chars with letters &amp; digits">
                    <span class="field-help">Min. 8 chars, letter + digit</span>
                    <c:if test="${not empty errors['password']}">
                        <span id="password-error" class="field-error" role="alert"><c:out value="${errors['password']}"/></span>
                    </c:if>
                </div>

                <div class="form-group form-col">
                    <label for="confirmPassword" class="form-label">Confirm Password <span class="required-indicator" aria-hidden="true">*</span></label>
                    <input type="password" 
                           id="confirmPassword" 
                           name="confirmPassword" 
                           class="form-control ${not empty errors['confirmPassword'] ? 'is-invalid' : ''}"
                           required 
                           minlength="8" 
                           maxlength="128" 
                           autocomplete="new-password"
                           aria-required="true"
                           <c:if test="${not empty errors['confirmPassword']}">aria-invalid="true" aria-describedby="confirmPassword-error"</c:if>
                           placeholder="Re-enter password">
                    <c:if test="${not empty errors['confirmPassword']}">
                        <span id="confirmPassword-error" class="field-error" role="alert"><c:out value="${errors['confirmPassword']}"/></span>
                    </c:if>
                </div>
            </div>

            <button type="submit" class="btn btn-primary btn-block btn-lg auth-submit-btn">
                Initialize Administrator Account
            </button>
        </form>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
