<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Sales Summary - ShopSphere</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<jsp:include page="/fragments/navbar.jsp" />

<div class="container py-4">
    <h3 class="mb-3">Admin Panel</h3>
    <jsp:include page="/fragments/admin-nav.jsp"><jsp:param name="active" value="sales"/></jsp:include>

    <h5 class="mb-3">Sales summary</h5>

    <!-- Numbers -->
    <div class="row g-3 mb-4">
        <div class="col-6 col-lg-3">
            <div class="card shadow-sm text-center">
                <div class="card-body">
                    <div class="text-muted small">Revenue</div>
                    <div class="fs-3 fw-bold">&#8377;<fmt:formatNumber value="${summary.revenue}" pattern="#,##0.00"/></div>
                </div>
            </div>
        </div>
        <div class="col-6 col-lg-3">
            <div class="card shadow-sm text-center">
                <div class="card-body">
                    <div class="text-muted small">Orders (not cancelled)</div>
                    <div class="fs-3 fw-bold">${summary.validOrders}</div>
                </div>
            </div>
        </div>
        <div class="col-6 col-lg-3">
            <div class="card shadow-sm text-center">
                <div class="card-body">
                    <div class="text-muted small">Average order value</div>
                    <div class="fs-3 fw-bold">&#8377;<fmt:formatNumber value="${summary.average}" pattern="#,##0.00"/></div>
                </div>
            </div>
        </div>
        <div class="col-6 col-lg-3">
            <div class="card shadow-sm text-center">
                <div class="card-body">
                    <div class="text-muted small">Cancelled orders</div>
                    <div class="fs-3 fw-bold">${summary.cancelledOrders}</div>
                </div>
            </div>
        </div>
    </div>

    <div class="row g-4">

        <!-- Top products -->
        <div class="col-lg-7">
            <div class="card shadow-sm mb-4">
                <div class="card-body">
                    <h5 class="card-title">Top selling products</h5>
                    <c:choose>
                        <c:when test="${empty topProducts}">
                            <p class="text-muted mb-0">No sales yet.</p>
                        </c:when>
                        <c:otherwise>
                            <div class="table-responsive">
                                <table class="table table-sm align-middle mb-0">
                                    <thead>
                                        <tr><th>#</th><th>Product</th><th>Units sold</th><th class="text-end">Revenue</th></tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="row" items="${topProducts}" varStatus="s">
                                            <tr>
                                                <td>${s.count}</td>
                                                <td><c:out value="${row.name}"/></td>
                                                <td>${row.units}</td>
                                                <td class="text-end">&#8377;<fmt:formatNumber value="${row.revenue}" pattern="#,##0.00"/></td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <div class="card shadow-sm">
                <div class="card-body">
                    <h5 class="card-title">Last 7 days</h5>
                    <c:choose>
                        <c:when test="${empty dailySales}">
                            <p class="text-muted mb-0">No orders in the last 7 days.</p>
                        </c:when>
                        <c:otherwise>
                            <div class="table-responsive">
                                <table class="table table-sm align-middle mb-0">
                                    <thead>
                                        <tr><th>Date</th><th>Orders</th><th class="text-end">Revenue</th></tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="row" items="${dailySales}">
                                            <tr>
                                                <td><fmt:formatDate value="${row.date}" pattern="dd MMM yyyy"/></td>
                                                <td>${row.orders}</td>
                                                <td class="text-end">&#8377;<fmt:formatNumber value="${row.revenue}" pattern="#,##0.00"/></td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                            <small class="text-muted">Only days with orders are shown.</small>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>

        <!-- Orders by status -->
        <div class="col-lg-5">
            <div class="card shadow-sm">
                <div class="card-body">
                    <h5 class="card-title">Orders by status</h5>
                    <c:choose>
                        <c:when test="${empty statusCounts}">
                            <p class="text-muted mb-0">No orders yet.</p>
                        </c:when>
                        <c:otherwise>
                            <ul class="list-group list-group-flush">
                                <c:forEach var="entry" items="${statusCounts}">
                                    <li class="list-group-item d-flex justify-content-between align-items-center px-0">
                                        <span><c:out value="${entry.key}"/></span>
                                        <span class="badge bg-secondary rounded-pill">${entry.value}</span>
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
