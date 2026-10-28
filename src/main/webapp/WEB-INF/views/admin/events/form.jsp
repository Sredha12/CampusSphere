<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="admin-page-container">
    <jsp:include page="/WEB-INF/views/admin/common/admin-nav.jsp"/>

    <div class="admin-form-card">
        <div class="form-card-header">
            <div class="breadcrumbs">
                <a href="${pageContext.request.contextPath}/admin/dashboard">Admin</a> &rsaquo; 
                <a href="${pageContext.request.contextPath}/admin/events">Events</a> &rsaquo; 
                <span><c:out value="${isEdit ? 'Edit Event' : 'Create Event'}"/></span>
            </div>
            <h1 class="form-card-title"><c:out value="${isEdit ? 'Edit Campus Event' : 'Create New Campus Event'}"/></h1>
            <p class="form-card-subtitle">
                <c:out value="${isEdit ? 'Update logistics, schedule, or attendee capacity for this event.' : 'Schedule a new event and configure registration parameters.'}"/>
            </p>
        </div>

        <c:choose>
            <c:when test="${empty clubs}">
                <div class="alert alert-error" role="alert">
                    <span class="alert-icon" aria-hidden="true">🏛️</span>
                    <div class="alert-text">
                        <strong>No Clubs Available:</strong> You must create at least one student club before scheduling an event.
                    </div>
                </div>
                <div class="form-actions">
                    <a href="${pageContext.request.contextPath}/admin/clubs/new" class="btn btn-primary">
                        Create a Club First
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/events" class="btn btn-secondary">
                        Back to Events
                    </a>
                </div>
            </c:when>

            <c:otherwise>
                <form action="${pageContext.request.contextPath}/admin/events/${isEdit ? 'edit' : 'new'}" method="post" class="admin-crud-form" novalidate>
                    <input type="hidden" name="_csrf" value="${csrfToken}">
                    <c:if test="${isEdit}">
                        <input type="hidden" name="id" value="${eventId}">
                    </c:if>

                    <c:if test="${not empty errors['general']}">
                        <div class="alert alert-error" role="alert">
                            <span class="alert-icon" aria-hidden="true">⚠</span>
                            <div class="alert-text"><c:out value="${errors['general']}"/></div>
                        </div>
                    </c:if>

                    <!-- Hosting Club & Title -->
                    <div class="form-row">
                        <div class="form-group form-col-2">
                            <label for="clubId" class="form-label">
                                Hosting Club <span class="required-indicator" aria-hidden="true">*</span>
                            </label>
                            <select id="clubId" 
                                    name="clubId" 
                                    class="form-control ${not empty errors['clubId'] ? 'is-invalid' : ''}" 
                                    required 
                                    aria-required="true"
                                    <c:if test="${not empty errors['clubId']}">aria-invalid="true" aria-describedby="clubId-error"</c:if>>
                                <option value="">-- Select Hosting Club --</option>
                                <c:forEach items="${clubs}" var="c">
                                    <option value="${c.id}" ${clubId eq c.id ? 'selected' : ''}>
                                        <c:out value="${c.name}"/>
                                    </option>
                                </c:forEach>
                            </select>
                            <c:if test="${not empty errors['clubId']}">
                                <span id="clubId-error" class="field-error" role="alert"><c:out value="${errors['clubId']}"/></span>
                            </c:if>
                        </div>

                        <div class="form-group form-col-2">
                            <label for="category" class="form-label">
                                Category <span class="required-indicator" aria-hidden="true">*</span>
                            </label>
                            <select id="category" 
                                    name="category" 
                                    class="form-control ${not empty errors['category'] ? 'is-invalid' : ''}" 
                                    required 
                                    aria-required="true"
                                    <c:if test="${not empty errors['category']}">aria-invalid="true" aria-describedby="category-error"</c:if>>
                                <option value="">-- Select Category --</option>
                                <c:forEach items="${categories}" var="cat">
                                    <option value="${cat}" ${category eq cat ? 'selected' : ''}>
                                        <c:out value="${cat}"/>
                                    </option>
                                </c:forEach>
                            </select>
                            <c:if test="${not empty errors['category']}">
                                <span id="category-error" class="field-error" role="alert"><c:out value="${errors['category']}"/></span>
                            </c:if>
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="title" class="form-label">
                            Event Title <span class="required-indicator" aria-hidden="true">*</span>
                        </label>
                        <input type="text" 
                               id="title" 
                               name="title" 
                               class="form-control ${not empty errors['title'] ? 'is-invalid' : ''}" 
                               value="<c:out value="${title}"/>" 
                               required 
                               maxlength="150" 
                               placeholder="e.g. Annual Campus Hackathon 2026"
                               aria-required="true"
                               <c:if test="${not empty errors['title']}">aria-invalid="true" aria-describedby="title-error"</c:if>>
                        <c:if test="${not empty errors['title']}">
                            <span id="title-error" class="field-error" role="alert"><c:out value="${errors['title']}"/></span>
                        </c:if>
                        <span class="field-help">Clear and descriptive title up to 150 characters.</span>
                    </div>

                    <div class="form-group">
                        <label for="description" class="form-label">
                            Description <span class="required-indicator" aria-hidden="true">*</span>
                        </label>
                        <textarea id="description" 
                                  name="description" 
                                  class="form-control form-textarea ${not empty errors['description'] ? 'is-invalid' : ''}" 
                                  rows="6" 
                                  required 
                                  maxlength="4000" 
                                  placeholder="Detail event agenda, guest speakers, prerequisites, team rules, and prizes..."
                                  aria-required="true"
                                  <c:if test="${not empty errors['description']}">aria-invalid="true" aria-describedby="desc-error"</c:if>><c:out value="${description}"/></textarea>
                        <c:if test="${not empty errors['description']}">
                            <span id="desc-error" class="field-error" role="alert"><c:out value="${errors['description']}"/></span>
                        </c:if>
                        <span class="field-help">Maximum 4,000 characters.</span>
                    </div>

                    <!-- Venue & Schedule -->
                    <div class="form-group">
                        <label for="venue" class="form-label">
                            Venue / Location <span class="required-indicator" aria-hidden="true">*</span>
                        </label>
                        <input type="text" 
                               id="venue" 
                               name="venue" 
                               class="form-control ${not empty errors['venue'] ? 'is-invalid' : ''}" 
                               value="<c:out value="${venue}"/>" 
                               required 
                               maxlength="150" 
                               placeholder="e.g. Main Auditorium, Block A, Room 302"
                               aria-required="true"
                               <c:if test="${not empty errors['venue']}">aria-invalid="true" aria-describedby="venue-error"</c:if>>
                        <c:if test="${not empty errors['venue']}">
                            <span id="venue-error" class="field-error" role="alert"><c:out value="${errors['venue']}"/></span>
                        </c:if>
                    </div>

                    <div class="form-row">
                        <div class="form-group form-col-3">
                            <label for="eventDate" class="form-label">
                                Event Date <span class="required-indicator" aria-hidden="true">*</span>
                            </label>
                            <input type="date" 
                                   id="eventDate" 
                                   name="eventDate" 
                                   class="form-control ${not empty errors['eventDate'] ? 'is-invalid' : ''}" 
                                   value="<c:out value="${eventDate}"/>" 
                                   required 
                                   aria-required="true"
                                   <c:if test="${not empty errors['eventDate']}">aria-invalid="true" aria-describedby="date-error"</c:if>>
                            <c:if test="${not empty errors['eventDate']}">
                                <span id="date-error" class="field-error" role="alert"><c:out value="${errors['eventDate']}"/></span>
                            </c:if>
                        </div>

                        <div class="form-group form-col-3">
                            <label for="startTime" class="form-label">
                                Start Time <span class="required-indicator" aria-hidden="true">*</span>
                            </label>
                            <input type="time" 
                                   id="startTime" 
                                   name="startTime" 
                                   class="form-control ${not empty errors['startTime'] ? 'is-invalid' : ''}" 
                                   value="<c:out value="${startTime}"/>" 
                                   required 
                                   aria-required="true"
                                   <c:if test="${not empty errors['startTime']}">aria-invalid="true" aria-describedby="start-error"</c:if>>
                            <c:if test="${not empty errors['startTime']}">
                                <span id="start-error" class="field-error" role="alert"><c:out value="${errors['startTime']}"/></span>
                            </c:if>
                        </div>

                        <div class="form-group form-col-3">
                            <label for="endTime" class="form-label">
                                End Time <span class="required-indicator" aria-hidden="true">*</span>
                            </label>
                            <input type="time" 
                                   id="endTime" 
                                   name="endTime" 
                                   class="form-control ${not empty errors['endTime'] ? 'is-invalid' : ''}" 
                                   value="<c:out value="${endTime}"/>" 
                                   required 
                                   aria-required="true"
                                   <c:if test="${not empty errors['endTime']}">aria-invalid="true" aria-describedby="end-error"</c:if>>
                            <c:if test="${not empty errors['endTime']}">
                                <span id="end-error" class="field-error" role="alert"><c:out value="${errors['endTime']}"/></span>
                            </c:if>
                        </div>
                    </div>

                    <!-- Deadline & Capacity -->
                    <div class="form-row">
                        <div class="form-group form-col-2">
                            <label for="registrationDeadline" class="form-label">
                                Registration Deadline <span class="required-indicator" aria-hidden="true">*</span>
                            </label>
                            <input type="datetime-local" 
                                   id="registrationDeadline" 
                                   name="registrationDeadline" 
                                   class="form-control ${not empty errors['registrationDeadline'] ? 'is-invalid' : ''}" 
                                   value="<c:out value="${registrationDeadline}"/>" 
                                   required 
                                   aria-required="true"
                                   <c:if test="${not empty errors['registrationDeadline']}">aria-invalid="true" aria-describedby="deadline-error"</c:if>>
                            <c:if test="${not empty errors['registrationDeadline']}">
                                <span id="deadline-error" class="field-error" role="alert"><c:out value="${errors['registrationDeadline']}"/></span>
                            </c:if>
                            <span class="field-help">Must not be after the event start time.</span>
                        </div>

                        <div class="form-group form-col-2">
                            <label for="capacity" class="form-label">
                                Attendee Capacity (Seats)
                            </label>
                            <input type="number" 
                                   id="capacity" 
                                   name="capacity" 
                                   class="form-control ${not empty errors['capacity'] ? 'is-invalid' : ''}" 
                                   value="<c:out value="${capacity}"/>" 
                                   min="1" 
                                   max="100000" 
                                   placeholder="Leave blank for unlimited"
                                   <c:if test="${not empty errors['capacity']}">aria-invalid="true" aria-describedby="cap-error"</c:if>>
                            <c:if test="${not empty errors['capacity']}">
                                <span id="cap-error" class="field-error" role="alert"><c:out value="${errors['capacity']}"/></span>
                            </c:if>
                            <span class="field-help">
                                <c:choose>
                                    <c:when test="${isEdit && not empty registeredCount}">
                                        Currently <strong><c:out value="${registeredCount}"/></strong> attendees registered. Capacity cannot be lower than this.
                                    </c:when>
                                    <c:otherwise>
                                        Leave blank for unlimited registration.
                                    </c:otherwise>
                                </c:choose>
                            </span>
                        </div>
                    </div>

                    <!-- Image URL -->
                    <div class="form-group">
                        <label for="imageUrl" class="form-label">Local Image Path</label>
                        <input type="text" 
                               id="imageUrl" 
                               name="imageUrl" 
                               class="form-control ${not empty errors['imageUrl'] ? 'is-invalid' : ''}" 
                               value="<c:out value="${imageUrl}"/>" 
                               maxlength="255" 
                               placeholder="e.g. assets/images/events/hackathon.jpg"
                               <c:if test="${not empty errors['imageUrl']}">aria-invalid="true" aria-describedby="image-error"</c:if>>
                        <c:if test="${not empty errors['imageUrl']}">
                            <span id="image-error" class="field-error" role="alert"><c:out value="${errors['imageUrl']}"/></span>
                        </c:if>
                        <span class="field-help">Optional local path (must start with <code>assets/</code> or <code>/</code>).</span>
                    </div>

                    <div class="form-actions">
                        <button type="submit" class="btn btn-primary">
                            <c:out value="${isEdit ? 'Save Changes' : 'Create Event'}"/>
                        </button>
                        <a href="${pageContext.request.contextPath}/admin/events" class="btn btn-secondary">
                            Cancel
                        </a>
                    </div>
                </form>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
