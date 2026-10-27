<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="auth-page-wrapper">
    <div class="auth-card">
        <div class="auth-header">
            <span class="auth-icon" aria-hidden="true">🎓</span>
            <h1 class="auth-title">Student Registration</h1>
            <p class="auth-subtitle">Create an account to discover and participate in campus events.</p>
        </div>

        <form action="${pageContext.request.contextPath}/register" method="post" class="auth-form" novalidate>
            <input type="hidden" name="_csrf" value="${csrfToken}">

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
                       <c:if test="${not empty errors['fullName']}">aria-invalid="true" aria-describedby="fullName-error"</c:if>
                       placeholder="e.g. Alex Johnson">
                <c:if test="${not empty errors['fullName']}">
                    <span id="fullName-error" class="field-error" role="alert"><c:out value="${errors['fullName']}"/></span>
                </c:if>
            </div>

            <div class="form-group">
                <label for="email" class="form-label">Email Address <span class="required-indicator" aria-hidden="true">*</span></label>
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
                       placeholder="e.g. alex.j@college.edu">
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
                    <span class="field-help">Min. 8 characters with at least one letter and one number</span>
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
                           <c:if test="${not empty errors['department']}">aria-invalid="true" aria-describedby="department-error"</c:if>
                           placeholder="e.g. Computer Science &amp; Engineering">
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
                        <option value="1" ${yearOfStudy eq '1' ? 'selected' : ''}>Year 1</option>
                        <option value="2" ${yearOfStudy eq '2' ? 'selected' : ''}>Year 2</option>
                        <option value="3" ${yearOfStudy eq '3' ? 'selected' : ''}>Year 3</option>
                        <option value="4" ${yearOfStudy eq '4' ? 'selected' : ''}>Year 4</option>
                        <option value="5" ${yearOfStudy eq '5' ? 'selected' : ''}>Year 5</option>
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
                       <c:if test="${not empty errors['phone']}">aria-invalid="true" aria-describedby="phone-error"</c:if>
                       placeholder="e.g. +91 9876543210">
                <span class="field-help">10 to 15 digits, optional leading +</span>
                <c:if test="${not empty errors['phone']}">
                    <span id="phone-error" class="field-error" role="alert"><c:out value="${errors['phone']}"/></span>
                </c:if>
            </div>

            <button type="submit" class="btn btn-primary btn-block btn-lg auth-submit-btn">
                Create Student Account
            </button>
        </form>

        <div class="auth-footer-links">
            <p>Already have an account? <a href="${pageContext.request.contextPath}/login">Log in here</a></p>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
