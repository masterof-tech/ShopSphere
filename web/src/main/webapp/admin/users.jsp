<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Manage Users - ShopSphere</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<jsp:include page="/fragments/navbar.jsp" />

<div class="container py-4">
    <h3 class="mb-3">Admin Panel</h3>
    <jsp:include page="/fragments/admin-nav.jsp"><jsp:param name="active" value="users"/></jsp:include>

    <c:if test="${not empty adminMessage}">
        <div class="alert alert-${adminMessageType}"><c:out value="${adminMessage}"/></div>
    </c:if>

    <h5 class="mb-3">Users (${users.size()})</h5>

    <div class="table-responsive">
        <table class="table align-middle bg-white shadow-sm">
            <thead class="table-light">
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Mobile</th>
                    <th>Role</th>
                    <th>Orders</th>
                    <th>Joined</th>
                    <th>Status</th>
                    <th></th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="u" items="${users}">
                    <tr class="${u.status ? '' : 'table-secondary'}">
                        <td>${u.userId}</td>
                        <td><c:out value="${u.name}"/></td>
                        <td><c:out value="${u.email}"/></td>
                        <td><c:out value="${u.mobile}"/></td>
                        <td>
                            <span class="badge ${u.role == 'ADMIN' ? 'bg-warning text-dark' : 'bg-secondary'}"><c:out value="${u.role}"/></span>
                        </td>
                        <td>${u.orderCount}</td>
                        <td><fmt:formatDate value="${u.createdAt}" pattern="dd MMM yyyy"/></td>
                        <td>
                            <span class="badge ${u.status ? 'bg-success' : 'bg-danger'}">${u.status ? 'Active' : 'Blocked'}</span>
                        </td>
                        <td>
                            <c:if test="${u.role == 'CUSTOMER'}">
                                <form action="${pageContext.request.contextPath}/admin/users" method="post">
                                    <input type="hidden" name="action" value="toggle">
                                    <input type="hidden" name="userId" value="${u.userId}">
                                    <input type="hidden" name="status" value="${u.status ? 'block' : 'unblock'}">
                                    <button type="submit" class="btn btn-sm ${u.status ? 'btn-outline-danger' : 'btn-outline-success'}">
                                        ${u.status ? 'Block' : 'Unblock'}
                                    </button>
                                </form>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
    <small class="text-muted">A blocked customer cannot log in. Admin accounts cannot be blocked.</small>
</div>

<jsp:include page="/fragments/footer.jsp" />
</body>
</html>
