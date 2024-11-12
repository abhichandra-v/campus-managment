<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>
<div class="card">
    <h2>Welcome, <c:out value="${sessionScope.currentUser.fullName}"/></h2>
    <p class="muted">Faculty dashboard. Course roster, grading and attendance views are built out in
        later stages of this reconstruction.</p>
</div>
<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
