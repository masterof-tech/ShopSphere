<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Manage Products - ShopSphere</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<jsp:include page="/fragments/navbar.jsp" />

<div class="container py-4">
    <h3 class="mb-3">Admin Panel</h3>
    <jsp:include page="/fragments/admin-nav.jsp"><jsp:param name="active" value="products"/></jsp:include>

    <c:if test="${not empty adminMessage}">
        <div class="alert alert-${adminMessageType}"><c:out value="${adminMessage}"/></div>
    </c:if>

    <div class="d-flex justify-content-between align-items-center mb-3">
        <h5 class="mb-0">Products (${products.size()})</h5>
        <a class="btn btn-primary btn-sm" href="${pageContext.request.contextPath}/admin/products?action=new">+ Add Product</a>
    </div>

    <div class="table-responsive">
        <table class="table align-middle bg-white shadow-sm">
            <thead class="table-light">
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Category</th>
                    <th>Price</th>
                    <th>Discount</th>
                    <th>Stock</th>
                    <th>Status</th>
                    <th></th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="p" items="${products}">
                    <tr class="${p.status ? '' : 'table-secondary'}">
                        <td>${p.productId}</td>
                        <td>
                            <c:out value="${p.name}"/><br>
                            <small class="text-muted"><c:out value="${p.brand}"/></small>
                        </td>
                        <td><c:out value="${categoryNames[p.categoryId]}"/></td>
                        <td>&#8377;<fmt:formatNumber value="${p.price}" pattern="#,##0.00"/></td>
                        <td><fmt:formatNumber value="${p.discount}" maxFractionDigits="0"/>%</td>
                        <td>
                            <span class="${p.stock <= 5 ? 'text-danger fw-bold' : ''}">${p.stock}</span>
                        </td>
                        <td>
                            <span class="badge ${p.status ? 'bg-success' : 'bg-secondary'}">${p.status ? 'Active' : 'Disabled'}</span>
                        </td>
                        <td class="text-nowrap">
                            <a class="btn btn-sm btn-outline-primary"
                               href="${pageContext.request.contextPath}/admin/products?action=edit&id=${p.productId}">Edit</a>

                            <form class="d-inline" action="${pageContext.request.contextPath}/admin/products" method="post">
                                <input type="hidden" name="action" value="toggle">
                                <input type="hidden" name="productId" value="${p.productId}">
                                <input type="hidden" name="status" value="${p.status ? 'disable' : 'enable'}">
                                <button type="submit" class="btn btn-sm ${p.status ? 'btn-outline-danger' : 'btn-outline-success'}">
                                    ${p.status ? 'Disable' : 'Enable'}
                                </button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
    <small class="text-muted">Disabled products are hidden from customers but old orders keep their record.</small>
</div>

<jsp:include page="/fragments/footer.jsp" />
</body>
</html>
