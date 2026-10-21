<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>My Orders - ShopSphere</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<jsp:include page="/fragments/navbar.jsp" />

<div class="container py-4">
    <h3 class="mb-4">My Orders</h3>

    <c:if test="${not empty orderMessage}">
        <div class="alert alert-${orderMessageType}"><c:out value="${orderMessage}"/></div>
    </c:if>

    <c:choose>
        <c:when test="${empty orders}">
            <div class="card shadow-sm">
                <div class="card-body text-center py-5">
                    <p class="lead mb-3">You have not placed any orders yet.</p>
                    <a class="btn btn-primary" href="${pageContext.request.contextPath}/products">Browse Products</a>
                </div>
            </div>
        </c:when>

        <c:otherwise>
            <div class="table-responsive">
                <table class="table align-middle bg-white shadow-sm">
                    <thead class="table-light">
                        <tr>
                            <th>Order</th>
                            <th>Date</th>
                            <th>Total</th>
                            <th>Payment</th>
                            <th>Status</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="o" items="${orders}">
                            <tr>
                                <td>#${o.orderId}</td>
                                <td><fmt:formatDate value="${o.createdAt}" pattern="dd MMM yyyy"/></td>
                                <td>&#8377;<fmt:formatNumber value="${o.totalAmount}" pattern="#,##0.00"/></td>
                                <td>
                                    ${o.paymentMethod == 'COD' ? 'Cash on Delivery' : o.paymentMethod}
                                    <br><small class="text-muted"><c:out value="${o.paymentStatus}"/></small>
                                </td>
                                <td>
                                    <span class="badge ${o.orderStatus == 'CANCELLED' ? 'bg-danger' : (o.orderStatus == 'DELIVERED' ? 'bg-success' : 'bg-primary')}">
                                        <c:out value="${o.orderStatus}"/>
                                    </span>
                                </td>
                                <td>
                                    <a class="btn btn-sm btn-outline-primary"
                                       href="${pageContext.request.contextPath}/order?id=${o.orderId}">View</a>
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
