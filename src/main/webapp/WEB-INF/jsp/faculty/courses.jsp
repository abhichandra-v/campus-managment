<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>
<div class="card">
    <h2>My Courses</h2>
    <c:choose>
        <c:when test="${empty courses}">
            <p class="muted">You are not currently assigned to any courses.</p>
        </c:when>
        <c:otherwise>
            <table>
                <thead>
                <tr>
                    <th>Code</th>
                    <th>Title</th>
                    <th>Semester</th>
                    <th>Enrolled</th>
                    <th></th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="course" items="${courses}">
                    <tr>
                        <td><c:out value="${course.code}"/></td>
                        <td><c:out value="${course.title}"/></td>
                        <td><c:out value="${course.semester}"/></td>
                        <td><c:out value="${course.enrolledCount}"/> / <c:out value="${course.capacity}"/></td>
                        <td>
                            <a class="btn" href="${pageContext.request.contextPath}/faculty/roster?courseId=${course.id}">Roster</a>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
</div>
<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
