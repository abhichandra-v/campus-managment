<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>
<div class="card">
    <h2>My Attendance</h2>
    <c:choose>
        <c:when test="${empty records}">
            <p class="muted">No attendance has been recorded yet.</p>
        </c:when>
        <c:otherwise>
            <table>
                <thead>
                <tr>
                    <th>Code</th>
                    <th>Title</th>
                    <th>Date</th>
                    <th>Status</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="r" items="${records}">
                    <tr>
                        <td><c:out value="${r.courseCode}"/></td>
                        <td><c:out value="${r.courseTitle}"/></td>
                        <td><c:out value="${r.attendanceDate}"/></td>
                        <td><span class="badge badge-${fn:toLowerCase(r.status)}"><c:out value="${r.status}"/></span></td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
</div>
<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
