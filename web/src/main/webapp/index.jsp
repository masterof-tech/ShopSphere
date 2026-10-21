<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>ShopSphere - Home</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>

<jsp:include page="/fragments/navbar.jsp" />

<div class="container py-5 text-center">
    <h1 class="display-5">Welcome to ShopSphere</h1>
    <p class="lead text-muted">Electronics, fashion, books and more, all in one place.</p>

    <c:if test="${not empty sessionScope.user}">
        <div class="alert alert-success d-inline-block">
            You are signed in as <strong><c:out value="${sessionScope.user.name}"/></strong>.
        </div>
        <br>
    </c:if>

    <a class="btn btn-primary btn-lg mt-3" href="${pageContext.request.contextPath}/products">Browse Products</a>
</div>

<jsp:include page="/fragments/footer.jsp" />
</body>
</html>
