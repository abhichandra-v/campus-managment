<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>
<div class="card">
    <h2>Welcome, <c:out value="${sessionScope.currentUser.fullName}"/></h2>
    <p><strong>Active enrollments:</strong> <c:out value="${activeCourseCount}"/></p>
    <div class="actions-row" style="margin-top:1rem;">
        <a class="btn" href="${pageContext.request.contextPath}/student/courses">Enroll in Courses</a>
        <a class="btn" href="${pageContext.request.contextPath}/student/enrollments">My Courses</a>
        <a class="btn" href="${pageContext.request.contextPath}/student/grades">Grades</a>
        <a class="btn" href="${pageContext.request.contextPath}/student/attendance">Attendance</a>
    </div>
</div>
<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
