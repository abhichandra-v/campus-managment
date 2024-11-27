<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>
<div class="card">
    <h2>My Courses</h2>
    <c:choose>
        <c:when test="${empty enrollments}">
            <p class="muted">You are not enrolled in any courses yet.
                <a href="${pageContext.request.contextPath}/student/courses">Browse available courses</a>.</p>
        </c:when>
        <c:otherwise>
            <table>
                <thead>
                <tr>
                    <th>Code</th>
                    <th>Title</th>
                    <th>Status</th>
                    <th>Enrolled</th>
                    <th></th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="e" items="${enrollments}">
                    <tr>
                        <td><c:out value="${e.courseCode}"/></td>
                        <td><c:out value="${e.courseTitle}"/></td>
                        <td><span class="badge badge-${fn:toLowerCase(e.status)}"><c:out value="${e.status}"/></span></td>
                        <td><c:out value="${fn:substring(e.enrolledAt, 0, 16)}"/></td>
                        <td>
                            <c:if test="${e.status == 'ENROLLED'}">
                                <form method="post" action="${pageContext.request.contextPath}/student/enrollments">
                                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                    <input type="hidden" name="enrollmentId" value="${e.id}">
                                    <button type="submit" class="btn-danger"
                                            onclick="return confirm('Drop this course?');">Drop</button>
                                </form>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
</div>
<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
