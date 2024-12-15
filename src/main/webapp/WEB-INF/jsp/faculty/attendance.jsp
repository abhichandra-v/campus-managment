<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>
<div class="card">
    <h2>Attendance - <c:out value="${course.code}"/>: <c:out value="${course.title}"/></h2>

    <form method="get" action="${pageContext.request.contextPath}/faculty/attendance" class="actions-row" style="margin-bottom:1rem;">
        <input type="hidden" name="courseId" value="${course.id}">
        <label for="date" style="margin:0;">Date</label>
        <input type="date" id="date" name="date" value="${date}" onchange="this.form.submit()">
    </form>

    <c:choose>
        <c:when test="${empty roster}">
            <p class="muted">No active students are enrolled in this course.</p>
        </c:when>
        <c:otherwise>
            <form method="post" action="${pageContext.request.contextPath}/faculty/attendance">
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                <input type="hidden" name="courseId" value="${course.id}">
                <input type="hidden" name="date" value="${date}">
                <table>
                    <thead>
                    <tr>
                        <th>Student</th>
                        <th>Status</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="e" items="${roster}">
                        <tr>
                            <td><c:out value="${e.studentName}"/></td>
                            <td>
                                <select name="status_${e.id}">
                                    <option value="">-</option>
                                    <c:forEach var="s" items="${statuses}">
                                        <option value="${s}" ${statusByEnrollment[e.id] == s ? 'selected' : ''}>
                                            <c:out value="${s}"/>
                                        </option>
                                    </c:forEach>
                                </select>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
                <button type="submit" style="margin-top:1rem;">Save Attendance</button>
            </form>
        </c:otherwise>
    </c:choose>
</div>
<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
