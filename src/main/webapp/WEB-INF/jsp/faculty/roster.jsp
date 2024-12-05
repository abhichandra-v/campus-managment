<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>
<div class="card">
    <h2>Roster - <c:out value="${course.code}"/>: <c:out value="${course.title}"/></h2>
    <p class="muted"><c:out value="${course.semester}"/> &middot;
        <c:out value="${course.enrolledCount}"/> / <c:out value="${course.capacity}"/> enrolled</p>
    <c:choose>
        <c:when test="${empty roster}">
            <p class="muted">No students are enrolled in this course yet.</p>
        </c:when>
        <c:otherwise>
            <table>
                <thead>
                <tr>
                    <th>Student</th>
                    <th>Status</th>
                    <th>Grade</th>
                    <th></th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="e" items="${roster}">
                    <tr>
                        <td><c:out value="${e.studentName}"/></td>
                        <td><span class="badge badge-${fn:toLowerCase(e.status)}"><c:out value="${e.status}"/></span></td>
                        <td><c:out value="${gradeByEnrollment[e.id]}" default="Not graded"/></td>
                        <td>
                            <c:if test="${e.status != 'DROPPED'}">
                                <form method="post" action="${pageContext.request.contextPath}/faculty/roster"
                                      class="actions-row">
                                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                    <input type="hidden" name="courseId" value="${course.id}">
                                    <input type="hidden" name="enrollmentId" value="${e.id}">
                                    <select name="grade" required>
                                        <option value="">Select</option>
                                        <c:forEach var="g" items="${['A','A-','B+','B','B-','C+','C','C-','D+','D','F','I','W']}">
                                            <option value="${g}" ${gradeByEnrollment[e.id] == g ? 'selected' : ''}>${g}</option>
                                        </c:forEach>
                                    </select>
                                    <button type="submit">Save</button>
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
