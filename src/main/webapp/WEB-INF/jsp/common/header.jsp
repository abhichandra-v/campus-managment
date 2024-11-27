<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><c:out value="${pageTitle}" default="Campus Management System"/></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="topbar">
    <a class="brand" href="${pageContext.request.contextPath}/">Campus Management System</a>
    <div>
        <nav style="display:inline">
            <c:if test="${sessionScope.currentUser.role == 'STUDENT'}">
                <a href="${pageContext.request.contextPath}/student/dashboard">Dashboard</a>
                <a href="${pageContext.request.contextPath}/student/courses">Enroll</a>
                <a href="${pageContext.request.contextPath}/student/enrollments">My Courses</a>
                <a href="${pageContext.request.contextPath}/student/grades">Grades</a>
                <a href="${pageContext.request.contextPath}/student/attendance">Attendance</a>
                <a href="${pageContext.request.contextPath}/student/profile">Profile</a>
            </c:if>
            <c:if test="${sessionScope.currentUser.role == 'FACULTY'}">
                <a href="${pageContext.request.contextPath}/faculty/dashboard">Dashboard</a>
                <a href="${pageContext.request.contextPath}/faculty/courses">My Courses</a>
                <a href="${pageContext.request.contextPath}/faculty/schedule">Schedule</a>
            </c:if>
            <c:if test="${sessionScope.currentUser.role == 'ADMIN'}">
                <a href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a>
                <a href="${pageContext.request.contextPath}/admin/users">Users</a>
                <a href="${pageContext.request.contextPath}/admin/courses">Courses</a>
                <a href="${pageContext.request.contextPath}/admin/reports">Reports</a>
            </c:if>
        </nav>
        <c:if test="${not empty sessionScope.currentUser}">
            <span style="color:#d7dce3;margin-left:1.25rem;font-size:0.92rem;">
                <c:out value="${sessionScope.currentUser.fullName}"/>
            </span>
            <form class="logout-form" method="post" action="${pageContext.request.contextPath}/logout">
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                <button type="submit">Logout</button>
            </form>
        </c:if>
    </div>
</div>
<div class="container">
<c:if test="${not empty sessionScope.flashSuccess}">
    <div class="alert alert-success"><c:out value="${sessionScope.flashSuccess}"/></div>
    <c:remove var="flashSuccess" scope="session"/>
</c:if>
<c:if test="${not empty sessionScope.flashError}">
    <div class="alert alert-error"><c:out value="${sessionScope.flashError}"/></div>
    <c:remove var="flashError" scope="session"/>
</c:if>
