<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>
<div class="card">
    <h2>Manage Courses</h2>
    <div class="actions-row" style="margin-bottom:1rem;">
        <a class="btn" href="${pageContext.request.contextPath}/admin/courses/new">+ New Course</a>
    </div>
    <table>
        <thead>
        <tr>
            <th>Code</th>
            <th>Title</th>
            <th>Credits</th>
            <th>Semester</th>
            <th>Faculty</th>
            <th>Enrolled</th>
            <th></th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="course" items="${courses}">
            <tr>
                <td><c:out value="${course.code}"/></td>
                <td><c:out value="${course.title}"/></td>
                <td><c:out value="${course.credits}"/></td>
                <td><c:out value="${course.semester}"/></td>
                <td><c:out value="${course.facultyName}" default="Unassigned"/></td>
                <td><c:out value="${course.enrolledCount}"/> / <c:out value="${course.capacity}"/></td>
                <td>
                    <a class="btn" href="${pageContext.request.contextPath}/admin/courses/edit?courseId=${course.id}">Edit</a>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
