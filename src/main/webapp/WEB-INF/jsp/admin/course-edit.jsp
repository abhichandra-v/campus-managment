<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>
<div class="card" style="max-width:480px;">
    <h2>Edit Course - <c:out value="${course.code}"/></h2>
    <form method="post" action="${pageContext.request.contextPath}/admin/courses/edit">
        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
        <input type="hidden" name="courseId" value="${course.id}">
        <div class="form-group">
            <label for="code">Course code</label>
            <input type="text" id="code" name="code" value="${course.code}" required>
        </div>
        <div class="form-group">
            <label for="title">Title</label>
            <input type="text" id="title" name="title" value="${course.title}" required>
        </div>
        <div class="form-group">
            <label for="credits">Credits</label>
            <input type="number" id="credits" name="credits" value="${course.credits}" min="1" max="6" required>
        </div>
        <div class="form-group">
            <label for="capacity">Capacity</label>
            <input type="number" id="capacity" name="capacity" value="${course.capacity}" min="1" required>
        </div>
        <div class="form-group">
            <label for="semester">Semester</label>
            <input type="text" id="semester" name="semester" value="${course.semester}" required>
        </div>
        <div class="form-group">
            <label for="facultyId">Assigned faculty</label>
            <select id="facultyId" name="facultyId">
                <option value="">Unassigned</option>
                <c:forEach var="f" items="${facultyList}">
                    <option value="${f.userId}" ${course.facultyId == f.userId ? 'selected' : ''}>
                        <c:out value="${f.fullName}"/> (<c:out value="${f.department}"/>)
                    </option>
                </c:forEach>
            </select>
        </div>
        <button type="submit">Save Changes</button>
    </form>
</div>
<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
