<%@ page isErrorPage="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Page Not Found</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="container">
    <div class="card">
        <h2>404 - Page Not Found</h2>
        <p>The page you requested does not exist.</p>
        <a class="btn" href="${pageContext.request.contextPath}/">Go home</a>
    </div>
</div>
</body>
</html>
