<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>
<div class="card">
    <h2>Welcome, <c:out value="${sessionScope.currentUser.fullName}"/></h2>
    <div class="grid-2">
        <div>
            <p><strong>Students:</strong> <c:out value="${summary.totalStudents()}"/></p>
            <p><strong>Faculty:</strong> <c:out value="${summary.totalFaculty()}"/></p>
        </div>
        <div>
            <p><strong>Courses:</strong> <c:out value="${summary.totalCourses()}"/></p>
            <p><strong>Active enrollments:</strong> <c:out value="${summary.totalActiveEnrollments()}"/></p>
        </div>
    </div>
    <div class="actions-row" style="margin-top:1rem;">
        <a class="btn" href="${pageContext.request.contextPath}/admin/users">Manage Users</a>
        <a class="btn" href="${pageContext.request.contextPath}/admin/courses">Manage Courses</a>
        <a class="btn" href="${pageContext.request.contextPath}/admin/reports">View Reports</a>
    </div>
</div>
<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
