<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>
<div class="card">
    <h2>Enroll in Courses - <c:out value="${semester}"/></h2>
    <c:choose>
        <c:when test="${empty courses}">
            <p class="muted">No courses are currently available to enroll in for this semester.</p>
        </c:when>
        <c:otherwise>
            <table>
                <thead>
                <tr>
                    <th>Code</th>
                    <th>Title</th>
                    <th>Credits</th>
                    <th>Faculty</th>
                    <th>Seats</th>
                    <th></th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="course" items="${courses}">
                    <tr>
                        <td><c:out value="${course.code}"/></td>
                        <td><c:out value="${course.title}"/></td>
                        <td><c:out value="${course.credits}"/></td>
                        <td><c:out value="${course.facultyName}" default="TBD"/></td>
                        <td><c:out value="${course.enrolledCount}"/> / <c:out value="${course.capacity}"/></td>
                        <td>
                            <c:choose>
                                <c:when test="${course.enrolledCount ge course.capacity}">
                                    <span class="muted">Full</span>
                                </c:when>
                                <c:otherwise>
                                    <form method="post" action="${pageContext.request.contextPath}/student/courses">
                                        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                        <input type="hidden" name="courseId" value="${course.id}">
                                        <button type="submit">Enroll</button>
                                    </form>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
</div>
<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
