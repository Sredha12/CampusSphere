<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="page-header">
    <div class="breadcrumbs">
        <a href="${pageContext.request.contextPath}/student/dashboard">Dashboard</a> &rsaquo; 
        <span>My Profile</span>
    </div>
    <h1 class="page-title">Student Profile</h1>
    <p class="page-subtitle">Manage your personal information and academic affiliation.</p>
</div>

<div class="profile-layout">
    <div class="profile-card">
        <c:if test="${not empty genericError}">
            <div class="alert alert-error auth-alert" role="alert" aria-live="assertive">
                <span class="alert-icon" aria-hidden="true">⚠</span>
                <div class="alert-text"><c:out value="${genericError}"/></div>
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/student/profile" method="post" class="profile-form" novalidate>
            <input type="hidden" name="_csrf" value="${csrfToken}">

            <!-- Email is unique identifier and read-only -->
            <div class="form-group">
                <label for="email" class="form-label">Email Address (Read-Only)</label>
                <input type="email" 
                       id="email" 
                       class="form-control form-control-readonly" 
                       value="<c:out value="${email}"/>" 
                       readonly 
                       tabindex="-1"
                       aria-describedby="email-help">
                <span id="email-help" class="field-help">Your student email address cannot be modified. Contact administration for account changes.</span>
            </div>

            <div class="form-group">
                <label for="fullName" class="form-label">Full Name <span class="required-indicator" aria-hidden="true">*</span></label>
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
                       <c:if test="${not empty errors['fullName']}">aria-invalid="true" aria-describedby="fullName-error"</c:if>>
                <c:if test="${not empty errors['fullName']}">
                    <span id="fullName-error" class="field-error" role="alert"><c:out value="${errors['fullName']}"/></span>
                </c:if>
            </div>

            <div class="form-row">
                <div class="form-group form-col-2">
                    <label for="department" class="form-label">Department / Major <span class="required-indicator" aria-hidden="true">*</span></label>
                    <input type="text" 
                           id="department" 
                           name="department" 
                           class="form-control ${not empty errors['department'] ? 'is-invalid' : ''}"
                           value="<c:out value="${department}"/>" 
                           required 
                           minlength="2" 
                           maxlength="100" 
                           aria-required="true"
                           <c:if test="${not empty errors['department']}">aria-invalid="true" aria-describedby="department-error"</c:if>>
                    <c:if test="${not empty errors['department']}">
                        <span id="department-error" class="field-error" role="alert"><c:out value="${errors['department']}"/></span>
                    </c:if>
                </div>

                <div class="form-group form-col-1">
                    <label for="yearOfStudy" class="form-label">Year of Study <span class="required-indicator" aria-hidden="true">*</span></label>
                    <select id="yearOfStudy" 
                            name="yearOfStudy" 
                            class="form-control ${not empty errors['yearOfStudy'] ? 'is-invalid' : ''}"
                            required 
                            aria-required="true"
                            <c:if test="${not empty errors['yearOfStudy']}">aria-invalid="true" aria-describedby="yearOfStudy-error"</c:if>>
                        <option value="">Select</option>
                        <option value="1" ${yearOfStudy eq '1' || yearOfStudy == 1 ? 'selected' : ''}>Year 1</option>
                        <option value="2" ${yearOfStudy eq '2' || yearOfStudy == 2 ? 'selected' : ''}>Year 2</option>
                        <option value="3" ${yearOfStudy eq '3' || yearOfStudy == 3 ? 'selected' : ''}>Year 3</option>
                        <option value="4" ${yearOfStudy eq '4' || yearOfStudy == 4 ? 'selected' : ''}>Year 4</option>
                        <option value="5" ${yearOfStudy eq '5' || yearOfStudy == 5 ? 'selected' : ''}>Year 5</option>
                    </select>
                    <c:if test="${not empty errors['yearOfStudy']}">
                        <span id="yearOfStudy-error" class="field-error" role="alert"><c:out value="${errors['yearOfStudy']}"/></span>
                    </c:if>
                </div>
            </div>

            <div class="form-group">
                <label for="phone" class="form-label">Phone Number <span class="required-indicator" aria-hidden="true">*</span></label>
                <input type="tel" 
                       id="phone" 
                       name="phone" 
                       class="form-control ${not empty errors['phone'] ? 'is-invalid' : ''}"
                       value="<c:out value="${phone}"/>" 
                       required 
                       autocomplete="tel"
                       aria-required="true"
                       <c:if test="${not empty errors['phone']}">aria-invalid="true" aria-describedby="phone-error"</c:if>>
                <span class="field-help">10 to 15 digits (optional leading +)</span>
                <c:if test="${not empty errors['phone']}">
                    <span id="phone-error" class="field-error" role="alert"><c:out value="${errors['phone']}"/></span>
                </c:if>
            </div>

            <div class="form-actions">
                <button type="submit" class="btn btn-primary">Save Profile Changes</button>
                <a href="${pageContext.request.contextPath}/student/dashboard" class="btn btn-secondary">Cancel</a>
            </div>
        </form>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
