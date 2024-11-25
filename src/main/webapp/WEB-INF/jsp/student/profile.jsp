<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>
<div class="grid-2">
    <div class="card">
        <h2>My Profile</h2>
        <form method="post" action="${pageContext.request.contextPath}/student/profile">
            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
            <div class="form-group">
                <label for="fullName">Full name</label>
                <input type="text" id="fullName" name="fullName" value="${sessionScope.currentUser.fullName}" required>
            </div>
            <div class="form-group">
                <label for="email">Email</label>
                <input type="email" id="email" name="email" value="${sessionScope.currentUser.email}" required>
            </div>
            <div class="form-group">
                <label for="major">Major</label>
                <input type="text" id="major" name="major" value="${student.major}">
            </div>
            <div class="form-group">
                <label for="dateOfBirth">Date of birth</label>
                <input type="date" id="dateOfBirth" name="dateOfBirth" value="${student.dateOfBirth}">
            </div>
            <button type="submit">Save changes</button>
        </form>
    </div>
    <div class="card">
        <h2>Account details</h2>
        <p><strong>Username:</strong> <c:out value="${sessionScope.currentUser.username}"/></p>
        <p><strong>Student number:</strong> <c:out value="${student.studentNumber}"/></p>
        <p><strong>Enrollment year:</strong> <c:out value="${student.enrollmentYear}"/></p>
        <p class="muted">Username, student number and enrollment year are set by the registrar's office
            and cannot be changed here.</p>
    </div>
</div>
<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
