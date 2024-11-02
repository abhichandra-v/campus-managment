<%@ page isErrorPage="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Something Went Wrong</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="container">
    <div class="card">
        <h2>500 - Something Went Wrong</h2>
        <p>An unexpected error occurred. Please try again, and contact an administrator if the problem persists.</p>
        <a class="btn" href="${pageContext.request.contextPath}/">Go home</a>
    </div>
</div>
</body>
</html>
