<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>
<div class="card">
    <h2>Manage Users</h2>
    <div class="actions-row" style="margin-bottom:1rem;">
        <a class="btn" href="${pageContext.request.contextPath}/admin/users/new-student">+ New Student</a>
        <a class="btn" href="${pageContext.request.contextPath}/admin/users/new-faculty">+ New Faculty</a>
    </div>
    <table>
        <thead>
        <tr>
            <th>Username</th>
            <th>Full name</th>
            <th>Email</th>
            <th>Role</th>
            <th>Status</th>
            <th></th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="u" items="${users}">
            <tr>
                <td><c:out value="${u.username}"/></td>
                <td><c:out value="${u.fullName}"/></td>
                <td><c:out value="${u.email}"/></td>
                <td><c:out value="${u.role}"/></td>
                <td>
                    <c:choose>
                        <c:when test="${u.active}"><span class="badge badge-enrolled">Active</span></c:when>
                        <c:otherwise><span class="badge badge-dropped">Deactivated</span></c:otherwise>
                    </c:choose>
                </td>
                <td>
                    <c:if test="${u.id != sessionScope.currentUser.id}">
                        <form method="post" action="${pageContext.request.contextPath}/admin/users">
                            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                            <input type="hidden" name="userId" value="${u.id}">
                            <input type="hidden" name="active" value="${!u.active}">
                            <button type="submit" class="${u.active ? 'btn-danger' : ''}">
                                ${u.active ? 'Deactivate' : 'Reactivate'}
                            </button>
                        </form>
                    </c:if>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
