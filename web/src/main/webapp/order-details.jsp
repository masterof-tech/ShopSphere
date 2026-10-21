<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Order #${order.orderId} - ShopSphere</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<jsp:include page="/fragments/navbar.jsp" />

<div class="container py-4">
    <a href="${pageContext.request.contextPath}/orders" class="text-decoration-none">&larr; Back to my orders</a>

    <div class="d-flex justify-content-between align-items-center mt-3 mb-3">
        <h3 class="mb-0">Order #${order.orderId}</h3>
        <span class="badge fs-6 ${order.orderStatus == 'CANCELLED' ? 'bg-danger' : (order.orderStatus == 'DELIVERED' ? 'bg-success' : 'bg-primary')}">
            <c:out value="${order.orderStatus}"/>
        </span>
    </div>

    <c:if test="${not empty orderMessage}">
        <div class="alert alert-${orderMessageType}"><c:out value="${orderMessage}"/></div>
    </c:if>

    <div class="row">

        <!-- Items -->
        <div class="col-lg-8 mb-4">
            <div class="table-responsive">
                <table class="table align-middle bg-white shadow-sm">
                    <thead class="table-light">
                        <tr>
                            <th>Product</th>
                            <th>Price</th>
                            <th>Qty</th>
                            <th class="text-end">Subtotal</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="item" items="${items}">
                            <tr>
                                <td>
                                    <a class="text-decoration-none"
                                       href="${pageContext.request.contextPath}/product?id=${item.productId}">
                                        <c:out value="${item.productName}"/>
                                    </a>
                                </td>
                                <td>&#8377;<fmt:formatNumber value="${item.price}" pattern="#,##0.00"/></td>
                                <td>${item.quantity}</td>
                                <td class="text-end">&#8377;<fmt:formatNumber value="${item.subtotal}" pattern="#,##0.00"/></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>

        <!-- Summary -->
        <div class="col-lg-4">
            <div class="card shadow-sm mb-3">
                <div class="card-body">
                    <h5 class="card-title">Summary</h5>
                    <hr>
                    <div class="d-flex justify-content-between">
                        <span>Items total</span>
                        <span>&#8377;<fmt:formatNumber value="${itemsTotal}" pattern="#,##0.00"/></span>
                    </div>
                    <c:if test="${order.discount > 0}">
                        <div class="d-flex justify-content-between text-success">
                            <span>Coupon discount</span>
                            <span>- &#8377;<fmt:formatNumber value="${order.discount}" pattern="#,##0.00"/></span>
                        </div>
                    </c:if>
                    <hr>
                    <div class="d-flex justify-content-between fw-bold fs-5">
                        <span>Total</span>
                        <span>&#8377;<fmt:formatNumber value="${order.totalAmount}" pattern="#,##0.00"/></span>
                    </div>
                </div>
            </div>

            <div class="card shadow-sm mb-3">
                <div class="card-body">
                    <h6 class="card-title">Payment</h6>
                    <p class="mb-1">${order.paymentMethod == 'COD' ? 'Cash on Delivery' : order.paymentMethod}</p>
                    <small class="text-muted">Status: <c:out value="${order.paymentStatus}"/></small>
                </div>
            </div>

            <div class="card shadow-sm mb-3">
                <div class="card-body">
                    <h6 class="card-title">Delivery address</h6>
                    <c:choose>
                        <c:when test="${not empty address}">
                            <p class="mb-0">
                                <c:out value="${address.addressLine}"/>,<br>
                                <c:out value="${address.city}"/>, <c:out value="${address.state}"/>
                                - <c:out value="${address.pincode}"/>
                            </p>
                        </c:when>
                        <c:otherwise>
                            <p class="mb-0 text-muted">Address not available</p>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <p class="text-muted small">
                Placed on <fmt:formatDate value="${order.createdAt}" pattern="dd MMM yyyy, hh:mm a"/>
            </p>

            <c:if test="${order.orderStatus == 'PLACED'}">
                <form action="${pageContext.request.contextPath}/order" method="post"
                      onsubmit="return confirm('Are you sure you want to cancel this order?');">
                    <input type="hidden" name="action" value="cancel">
                    <input type="hidden" name="orderId" value="${order.orderId}">
                    <button type="submit" class="btn btn-outline-danger w-100">Cancel Order</button>
                </form>
            </c:if>
        </div>
    </div>
</div>

<jsp:include page="/fragments/footer.jsp" />
</body>
</html>
