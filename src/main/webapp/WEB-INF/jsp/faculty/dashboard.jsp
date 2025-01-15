<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>
<div class="card">
    <h2>Welcome, <c:out value="${sessionScope.currentUser.fullName}"/></h2>
    <p><strong>Courses this semester (<c:out value="${semester}"/>):</strong>
        <c:out value="${currentSemesterCourseCount}"/></p>
    <div class="actions-row" style="margin-top:1rem;">
        <a class="btn" href="${pageContext.request.contextPath}/faculty/courses">My Courses</a>
        <a class="btn" href="${pageContext.request.contextPath}/faculty/schedule">Schedule</a>
    </div>
</div>
<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
