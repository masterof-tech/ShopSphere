<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Manage Categories - ShopSphere</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<jsp:include page="/fragments/navbar.jsp" />

<div class="container py-4">
    <h3 class="mb-3">Admin Panel</h3>
    <jsp:include page="/fragments/admin-nav.jsp"><jsp:param name="active" value="categories"/></jsp:include>

    <c:if test="${not empty adminMessage}">
        <div class="alert alert-${adminMessageType}"><c:out value="${adminMessage}"/></div>
    </c:if>

    <div class="row g-4">

        <!-- List -->
        <div class="col-lg-7">
            <div class="table-responsive">
                <table class="table align-middle bg-white shadow-sm">
                    <thead class="table-light">
                        <tr>
                            <th>ID</th>
                            <th>Name</th>
                            <th>Status</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="cat" items="${categories}">
                            <tr class="${cat.status ? '' : 'table-secondary'}">
                                <td>${cat.categoryId}</td>
                                <td>
                                    <c:out value="${cat.name}"/><br>
                                    <small class="text-muted"><c:out value="${cat.description}"/></small>
                                </td>
                                <td>
                                    <span class="badge ${cat.status ? 'bg-success' : 'bg-secondary'}">${cat.status ? 'Active' : 'Disabled'}</span>
                                </td>
                                <td class="text-nowrap">
                                    <a class="btn btn-sm btn-outline-primary"
                                       href="${pageContext.request.contextPath}/admin/categories?edit=${cat.categoryId}">Edit</a>

                                    <form class="d-inline" action="${pageContext.request.contextPath}/admin/categories" method="post">
                                        <input type="hidden" name="action" value="toggle">
                                        <input type="hidden" name="categoryId" value="${cat.categoryId}">
                                        <input type="hidden" name="status" value="${cat.status ? 'disable' : 'enable'}">
                                        <button type="submit" class="btn btn-sm ${cat.status ? 'btn-outline-danger' : 'btn-outline-success'}">
                                            ${cat.status ? 'Disable' : 'Enable'}
                                        </button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
            <small class="text-muted">A disabled category is hidden from the product filter on the website.</small>
        </div>

        <!-- Add / edit form -->
        <div class="col-lg-5">
            <div class="card shadow-sm">
                <div class="card-body">
                    <h5 class="card-title">${not empty editCategory and editCategory.categoryId > 0 ? 'Edit category' : 'Add a category'}</h5>

                    <c:if test="${not empty error}">
                        <div class="alert alert-danger"><c:out value="${error}"/></div>
                    </c:if>

                    <form action="${pageContext.request.contextPath}/admin/categories" method="post">
                        <input type="hidden" name="action" value="save">
                        <input type="hidden" name="categoryId" value="${not empty editCategory ? editCategory.categoryId : 0}">

                        <div class="mb-3">
                            <label class="form-label">Name</label>
                            <input type="text" name="name" class="form-control" maxlength="80"
                                   value="<c:out value='${editCategory.name}'/>" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Description</label>
                            <input type="text" name="description" class="form-control" maxlength="255"
                                   value="<c:out value='${editCategory.description}'/>">
                        </div>

                        <button type="submit" class="btn btn-primary">Save category</button>
                        <c:if test="${not empty editCategory}">
                            <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/admin/categories">Cancel</a>
                        </c:if>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/fragments/footer.jsp" />
</body>
</html>
