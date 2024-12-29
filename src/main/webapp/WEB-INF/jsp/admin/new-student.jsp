<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>
<div class="card" style="max-width:520px;">
    <h2>Create Student Account</h2>
    <form method="post" action="${pageContext.request.contextPath}/admin/users/new-student">
        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
        <div class="form-group">
            <label for="username">Username</label>
            <input type="text" id="username" name="username" required>
        </div>
        <div class="form-group">
            <label for="password">Temporary password</label>
            <input type="password" id="password" name="password" required minlength="8">
        </div>
        <div class="form-group">
            <label for="fullName">Full name</label>
            <input type="text" id="fullName" name="fullName" required>
        </div>
        <div class="form-group">
            <label for="email">Email</label>
            <input type="email" id="email" name="email" required>
        </div>
        <div class="form-group">
            <label for="studentNumber">Student number</label>
            <input type="text" id="studentNumber" name="studentNumber" required>
        </div>
        <div class="form-group">
            <label for="enrollmentYear">Enrollment year</label>
            <input type="number" id="enrollmentYear" name="enrollmentYear" min="2000" max="2100" required>
        </div>
        <div class="form-group">
            <label for="major">Major</label>
            <input type="text" id="major" name="major">
        </div>
        <div class="form-group">
            <label for="dateOfBirth">Date of birth</label>
            <input type="date" id="dateOfBirth" name="dateOfBirth">
        </div>
        <button type="submit">Create Account</button>
    </form>
</div>
<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
