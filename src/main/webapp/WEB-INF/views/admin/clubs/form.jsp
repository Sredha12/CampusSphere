<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="admin-page-container">
    <jsp:include page="/WEB-INF/views/admin/common/admin-nav.jsp"/>

    <div class="admin-form-card">
        <div class="form-card-header">
            <div class="breadcrumbs">
                <a href="${pageContext.request.contextPath}/admin/dashboard">Admin</a> &rsaquo; 
                <a href="${pageContext.request.contextPath}/admin/clubs">Clubs</a> &rsaquo; 
                <span><c:out value="${isEdit ? 'Edit Club' : 'Create Club'}"/></span>
            </div>
            <h1 class="form-card-title"><c:out value="${isEdit ? 'Edit Student Club' : 'Create New Student Club'}"/></h1>
            <p class="form-card-subtitle">
                <c:out value="${isEdit ? 'Update details for this registered student society.' : 'Register a new student club or campus organization.'}"/>
            </p>
        </div>

        <form action="${pageContext.request.contextPath}/admin/clubs/${isEdit ? 'edit' : 'new'}" method="post" class="admin-crud-form" novalidate>
            <input type="hidden" name="_csrf" value="${csrfToken}">
            <c:if test="${isEdit}">
                <input type="hidden" name="id" value="${clubId}">
            </c:if>

            <div class="form-group">
                <label for="name" class="form-label">
                    Club Name <span class="required-indicator" aria-hidden="true">*</span>
                </label>
                <input type="text" 
                       id="name" 
                       name="name" 
                       class="form-control ${not empty errors['name'] ? 'is-invalid' : ''}" 
                       value="<c:out value="${name}"/>" 
                       required 
                       maxlength="100" 
                       placeholder="e.g. Robotics & Automation Society"
                       aria-required="true"
                       <c:if test="${not empty errors['name']}">aria-invalid="true" aria-describedby="name-error"</c:if>>
                <c:if test="${not empty errors['name']}">
                    <span id="name-error" class="field-error" role="alert"><c:out value="${errors['name']}"/></span>
                </c:if>
                <span class="field-help">Unique club title up to 100 characters.</span>
            </div>

            <div class="form-group">
                <label for="description" class="form-label">
                    Description <span class="required-indicator" aria-hidden="true">*</span>
                </label>
                <textarea id="description" 
                          name="description" 
                          class="form-control form-textarea ${not empty errors['description'] ? 'is-invalid' : ''}" 
                          rows="5" 
                          required 
                          maxlength="2000" 
                          placeholder="Provide a comprehensive summary of the club's mission, regular activities, and leadership..."
                          aria-required="true"
                          <c:if test="${not empty errors['description']}">aria-invalid="true" aria-describedby="description-error"</c:if>><c:out value="${description}"/></textarea>
                <c:if test="${not empty errors['description']}">
                    <span id="description-error" class="field-error" role="alert"><c:out value="${errors['description']}"/></span>
                </c:if>
                <span class="field-help">Maximum 2,000 characters.</span>
            </div>

            <div class="form-row">
                <div class="form-group form-col-2">
                    <label for="contactEmail" class="form-label">Contact Email</label>
                    <input type="email" 
                           id="contactEmail" 
                           name="contactEmail" 
                           class="form-control ${not empty errors['contactEmail'] ? 'is-invalid' : ''}" 
                           value="<c:out value="${contactEmail}"/>" 
                           maxlength="150" 
                           placeholder="e.g. robotics@campussphere.example"
                           <c:if test="${not empty errors['contactEmail']}">aria-invalid="true" aria-describedby="email-error"</c:if>>
                    <c:if test="${not empty errors['contactEmail']}">
                        <span id="email-error" class="field-error" role="alert"><c:out value="${errors['contactEmail']}"/></span>
                    </c:if>
                    <span class="field-help">Official student coordinator or faculty contact email.</span>
                </div>

                <div class="form-group form-col-2">
                    <label for="imageUrl" class="form-label">Local Image Path</label>
                    <input type="text" 
                           id="imageUrl" 
                           name="imageUrl" 
                           class="form-control ${not empty errors['imageUrl'] ? 'is-invalid' : ''}" 
                           value="<c:out value="${imageUrl}"/>" 
                           maxlength="255" 
                           placeholder="e.g. assets/images/clubs/robotics.png"
                           <c:if test="${not empty errors['imageUrl']}">aria-invalid="true" aria-describedby="image-error"</c:if>>
                    <c:if test="${not empty errors['imageUrl']}">
                        <span id="image-error" class="field-error" role="alert"><c:out value="${errors['imageUrl']}"/></span>
                    </c:if>
                    <span class="field-help">Optional local asset path (must start with <code>assets/</code> or <code>/</code>).</span>
                </div>
            </div>

            <div class="form-actions">
                <button type="submit" class="btn btn-primary">
                    <c:out value="${isEdit ? 'Save Changes' : 'Create Club'}"/>
                </button>
                <a href="${pageContext.request.contextPath}/admin/clubs" class="btn btn-secondary">
                    Cancel
                </a>
            </div>
        </form>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
