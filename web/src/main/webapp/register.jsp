<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Register - ShopSphere</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<div class="container py-5">
    <div class="row justify-content-center">
        <div class="col-md-6">
            <h2 class="text-center mb-4">Shop<span class="text-primary">Sphere</span></h2>
            <div class="card shadow-sm">
                <div class="card-body p-4">
                    <h4 class="mb-3">Naya account banao</h4>

                    <c:if test="${not empty error}">
                        <div class="alert alert-danger"><c:out value="${error}"/></div>
                    </c:if>

                    <form action="${pageContext.request.contextPath}/register" method="post">
                        <div class="mb-3">
                            <label class="form-label">Poora naam</label>
                            <input type="text" name="name" class="form-control"
                                   value="<c:out value='${param.name}'/>" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Email</label>
                            <input type="email" name="email" class="form-control"
                                   value="<c:out value='${param.email}'/>" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Mobile (10 digit)</label>
                            <input type="text" name="mobile" class="form-control" maxlength="10"
                                   value="<c:out value='${param.mobile}'/>" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Password (kam se kam 6 character)</label>
                            <input type="password" name="password" class="form-control" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Password dobara likho</label>
                            <input type="password" name="confirmPassword" class="form-control" required>
                        </div>
                        <button type="submit" class="btn btn-primary w-100">Register</button>
                    </form>

                    <p class="text-center mt-3 mb-0">
                        Pehle se account hai? <a href="${pageContext.request.contextPath}/login">Login karo</a>
                    </p>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>
