<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Manage Orders - ShopSphere</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<jsp:include page="/fragments/navbar.jsp" />

<div class="container py-4">
    <h3 class="mb-3">Admin Panel</h3>
    <jsp:include page="/fragments/admin-nav.jsp"><jsp:param name="active" value="orders"/></jsp:include>

    <c:if test="${not empty adminMessage}">
        <div class="alert alert-${adminMessageType}"><c:out value="${adminMessage}"/></div>
    </c:if>

    <!-- Status filter -->
    <div class="d-flex flex-wrap gap-2 mb-3">
        <a class="btn btn-sm ${empty selectedStatus ? 'btn-primary' : 'btn-outline-primary'}"
           href="${pageContext.request.contextPath}/admin/orders">All</a>
        <c:forEach var="s" items="${statuses}">
            <a class="btn btn-sm ${selectedStatus == s ? 'btn-primary' : 'btn-outline-primary'}"
               href="${pageContext.request.contextPath}/admin/orders?status=${s}"><c:out value="${s}"/></a>
        </c:forEach>
    </div>

    <c:choose>
        <c:when test="${empty orders}">
            <div class="card shadow-sm">
                <div class="card-body text-center py-5 text-muted">No orders found.</div>
            </div>
        </c:when>
        <c:otherwise>
            <div class="table-responsive">
                <table class="table align-middle bg-white shadow-sm">
                    <thead class="table-light">
                        <tr>
                            <th>Order</th>
                            <th>Customer</th>
                            <th>Date</th>
                            <th>Total</th>
                            <th>Payment</th>
                            <th>Status</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="row" items="${orders}">
                            <tr>
                                <td>#${row.orderId}</td>
                                <td><c:out value="${row.customer}"/></td>
                                <td><fmt:formatDate value="${row.createdAt}" pattern="dd MMM yyyy"/></td>
                                <td>&#8377;<fmt:formatNumber value="${row.total}" pattern="#,##0.00"/></td>
                                <td>
                                    ${row.paymentMethod == 'COD' ? 'Cash on Delivery' : row.paymentMethod}<br>
                                    <small class="text-muted"><c:out value="${row.paymentStatus}"/></small>
                                </td>
                                <td>
                                    <span class="badge ${row.status == 'CANCELLED' ? 'bg-danger' : (row.status == 'DELIVERED' ? 'bg-success' : (row.status == 'SHIPPED' ? 'bg-warning text-dark' : (row.status == 'CONFIRMED' ? 'bg-info text-dark' : 'bg-primary')))}">
                                        <c:out value="${row.status}"/>
                                    </span>
                                </td>
                                <td>
                                    <a class="btn btn-sm btn-outline-primary"
                                       href="${pageContext.request.contextPath}/admin/order?id=${row.orderId}">Manage</a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="/fragments/footer.jsp" />
</body>
</html>
