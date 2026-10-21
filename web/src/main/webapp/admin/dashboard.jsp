<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Admin Dashboard - ShopSphere</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<jsp:include page="/fragments/navbar.jsp" />

<div class="container py-4">
    <h3 class="mb-3">Admin Panel</h3>
    <jsp:include page="/fragments/admin-nav.jsp"><jsp:param name="active" value="dashboard"/></jsp:include>

    <!-- Numbers -->
    <div class="row g-3 mb-4">
        <div class="col-6 col-lg-3">
            <div class="card shadow-sm text-center">
                <div class="card-body">
                    <div class="text-muted small">Customers</div>
                    <div class="fs-2 fw-bold">${customerCount}</div>
                </div>
            </div>
        </div>
        <div class="col-6 col-lg-3">
            <div class="card shadow-sm text-center">
                <div class="card-body">
                    <div class="text-muted small">Products</div>
                    <div class="fs-2 fw-bold">${productCount}</div>
                </div>
            </div>
        </div>
        <div class="col-6 col-lg-3">
            <div class="card shadow-sm text-center">
                <div class="card-body">
                    <div class="text-muted small">Orders</div>
                    <div class="fs-2 fw-bold">${orderCount}</div>
                </div>
            </div>
        </div>
        <div class="col-6 col-lg-3">
            <div class="card shadow-sm text-center">
                <div class="card-body">
                    <div class="text-muted small">Revenue</div>
                    <div class="fs-2 fw-bold">&#8377;<fmt:formatNumber value="${revenue}" pattern="#,##0.00"/></div>
                </div>
            </div>
        </div>
    </div>

    <div class="row g-4">

        <!-- Recent orders -->
        <div class="col-lg-7">
            <div class="card shadow-sm">
                <div class="card-body">
                    <h5 class="card-title">Recent orders</h5>
                    <c:choose>
                        <c:when test="${empty recentOrders}">
                            <p class="text-muted mb-0">No orders yet.</p>
                        </c:when>
                        <c:otherwise>
                            <div class="table-responsive">
                                <table class="table table-sm align-middle mb-0">
                                    <thead>
                                        <tr><th>Order</th><th>Customer</th><th>Date</th><th>Total</th><th>Status</th></tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="row" items="${recentOrders}">
                                            <tr>
                                                <td>#${row.orderId}</td>
                                                <td><c:out value="${row.customer}"/></td>
                                                <td><fmt:formatDate value="${row.createdAt}" pattern="dd MMM yyyy"/></td>
                                                <td>&#8377;<fmt:formatNumber value="${row.total}" pattern="#,##0.00"/></td>
                                                <td>
                                                    <span class="badge ${row.status == 'CANCELLED' ? 'bg-danger' : (row.status == 'DELIVERED' ? 'bg-success' : 'bg-primary')}">
                                                        <c:out value="${row.status}"/>
                                                    </span>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>

        <!-- Low stock -->
        <div class="col-lg-5">
            <div class="card shadow-sm">
                <div class="card-body">
                    <h5 class="card-title">Low stock (${lowStockLimit} or fewer)</h5>
                    <c:choose>
                        <c:when test="${empty lowStockProducts}">
                            <p class="text-muted mb-0">All products have enough stock.</p>
                        </c:when>
                        <c:otherwise>
                            <ul class="list-group list-group-flush">
                                <c:forEach var="p" items="${lowStockProducts}">
                                    <li class="list-group-item d-flex justify-content-between align-items-center px-0">
                                        <a class="text-decoration-none"
                                           href="${pageContext.request.contextPath}/admin/products?action=edit&id=${p.productId}">
                                            <c:out value="${p.name}"/>
                                        </a>
                                        <span class="badge ${p.stock == 0 ? 'bg-danger' : 'bg-warning text-dark'}">${p.stock} left</span>
                                    </li>
                                </c:forEach>
                            </ul>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/fragments/footer.jsp" />
</body>
</html>
