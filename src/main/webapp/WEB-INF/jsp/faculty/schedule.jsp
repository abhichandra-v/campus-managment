<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>
<div class="card">
    <h2>My Schedule - <c:out value="${semester}"/></h2>
    <c:choose>
        <c:when test="${empty courses}">
            <p class="muted">You are not teaching any courses this semester.</p>
        </c:when>
        <c:otherwise>
            <table>
                <thead>
                <tr>
                    <th>Code</th>
                    <th>Title</th>
                    <th>Credits</th>
                    <th>Enrolled</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="course" items="${courses}">
                    <tr>
                        <td><c:out value="${course.code}"/></td>
                        <td><c:out value="${course.title}"/></td>
                        <td><c:out value="${course.credits}"/></td>
                        <td><c:out value="${course.enrolledCount}"/> / <c:out value="${course.capacity}"/></td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
            <p class="muted">Meeting days/times are not tracked in this system's data model.</p>
        </c:otherwise>
    </c:choose>
</div>
<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
