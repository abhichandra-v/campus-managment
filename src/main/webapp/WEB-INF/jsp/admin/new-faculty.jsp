<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>
<div class="card" style="max-width:520px;">
    <h2>Create Faculty Account</h2>
    <form method="post" action="${pageContext.request.contextPath}/admin/users/new-faculty">
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
            <label for="department">Department</label>
            <input type="text" id="department" name="department" required>
        </div>
        <div class="form-group">
            <label for="title">Title</label>
            <input type="text" id="title" name="title" placeholder="e.g. Associate Professor">
        </div>
        <div class="form-group">
            <label for="officeLocation">Office location</label>
            <input type="text" id="officeLocation" name="officeLocation">
        </div>
        <button type="submit">Create Account</button>
    </form>
</div>
<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
