<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>
<div class="card">
    <h2>System Summary</h2>
    <div class="grid-2">
        <div>
            <p><strong>Total students:</strong> <c:out value="${summary.totalStudents()}"/></p>
            <p><strong>Total faculty:</strong> <c:out value="${summary.totalFaculty()}"/></p>
        </div>
        <div>
            <p><strong>Total courses:</strong> <c:out value="${summary.totalCourses()}"/></p>
            <p><strong>Total active enrollments:</strong> <c:out value="${summary.totalActiveEnrollments()}"/></p>
        </div>
    </div>
</div>
<div class="card">
    <h2>Enrollment by Course</h2>
    <form method="get" action="${pageContext.request.contextPath}/admin/reports" class="actions-row" style="margin-bottom:1rem;">
        <label for="semester" style="margin:0;">Semester</label>
        <input type="text" id="semester" name="semester" value="${semester}" placeholder="e.g. FALL2024">
        <button type="submit">Filter</button>
        <a class="btn btn-secondary" href="${pageContext.request.contextPath}/admin/reports">Clear</a>
    </form>
    <table>
        <thead>
        <tr>
            <th>Code</th>
            <th>Title</th>
            <th>Semester</th>
            <th>Faculty</th>
            <th>Enrolled / Capacity</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="course" items="${courses}">
            <tr>
                <td><c:out value="${course.code}"/></td>
                <td><c:out value="${course.title}"/></td>
                <td><c:out value="${course.semester}"/></td>
                <td><c:out value="${course.facultyName}" default="Unassigned"/></td>
                <td><c:out value="${course.enrolledCount}"/> / <c:out value="${course.capacity}"/></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
