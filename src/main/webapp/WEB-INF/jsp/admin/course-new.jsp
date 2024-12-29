<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>
<div class="card" style="max-width:480px;">
    <h2>Create Course</h2>
    <form method="post" action="${pageContext.request.contextPath}/admin/courses/new">
        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
        <div class="form-group">
            <label for="code">Course code</label>
            <input type="text" id="code" name="code" placeholder="e.g. CS301" required>
        </div>
        <div class="form-group">
            <label for="title">Title</label>
            <input type="text" id="title" name="title" required>
        </div>
        <div class="form-group">
            <label for="credits">Credits</label>
            <input type="number" id="credits" name="credits" min="1" max="6" required>
        </div>
        <div class="form-group">
            <label for="capacity">Capacity</label>
            <input type="number" id="capacity" name="capacity" min="1" required>
        </div>
        <div class="form-group">
            <label for="semester">Semester</label>
            <input type="text" id="semester" name="semester" placeholder="e.g. FALL2024" required>
        </div>
        <button type="submit">Create Course</button>
    </form>
</div>
<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
