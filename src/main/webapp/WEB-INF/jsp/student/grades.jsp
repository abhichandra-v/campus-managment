<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>
<div class="card">
    <h2>My Grades</h2>
    <c:choose>
        <c:when test="${empty grades}">
            <p class="muted">No grades have been recorded yet.</p>
        </c:when>
        <c:otherwise>
            <table>
                <thead>
                <tr>
                    <th>Code</th>
                    <th>Title</th>
                    <th>Grade</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="g" items="${grades}">
                    <tr>
                        <td><c:out value="${g.courseCode}"/></td>
                        <td><c:out value="${g.courseTitle}"/></td>
                        <td><c:out value="${g.grade}" default="Not graded yet"/></td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
</div>
<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
