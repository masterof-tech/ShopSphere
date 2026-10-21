<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Order #${order.orderId} - ShopSphere Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<jsp:include page="/fragments/navbar.jsp" />

<div class="container py-4">
    <h3 class="mb-3">Admin Panel</h3>
    <jsp:include page="/fragments/admin-nav.jsp"><jsp:param name="active" value="orders"/></jsp:include>

    <a href="${pageContext.request.contextPath}/admin/orders" class="text-decoration-none">&larr; Back to all orders</a>

    <div class="d-flex justify-content-between align-items-center mt-3 mb-3">
        <h4 class="mb-0">Order #${order.orderId}</h4>
        <span class="badge fs-6 ${order.orderStatus == 'CANCELLED' ? 'bg-danger' : (order.orderStatus == 'DELIVERED' ? 'bg-success' : (order.orderStatus == 'SHIPPED' ? 'bg-warning text-dark' : (order.orderStatus == 'CONFIRMED' ? 'bg-info text-dark' : 'bg-primary')))}">
            <c:out value="${order.orderStatus}"/>
        </span>
    </div>

    <c:if test="${not empty adminMessage}">
        <div class="alert alert-${adminMessageType}"><c:out value="${adminMessage}"/></div>
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
                                <td><c:out value="${item.productName}"/></td>
                                <td>&#8377;<fmt:formatNumber value="${item.price}" pattern="#,##0.00"/></td>
                                <td>${item.quantity}</td>
                                <td class="text-end">&#8377;<fmt:formatNumber value="${item.subtotal}" pattern="#,##0.00"/></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>

            <!-- Actions -->
            <div class="card shadow-sm">
                <div class="card-body">
                    <h6 class="card-title">Update order</h6>

                    <c:choose>
                        <c:when test="${not empty nextStatus || canCancel}">
                            <div class="d-flex flex-wrap gap-2">
                                <c:if test="${not empty nextStatus}">
                                    <form action="${pageContext.request.contextPath}/admin/order" method="post">
                                        <input type="hidden" name="action" value="advance">
                                        <input type="hidden" name="orderId" value="${order.orderId}">
                                        <button type="submit" class="btn btn-success">
                                            <c:choose>
                                                <c:when test="${nextStatus == 'CONFIRMED'}">Confirm order</c:when>
                                                <c:when test="${nextStatus == 'SHIPPED'}">Mark as shipped</c:when>
                                                <c:otherwise>Mark as delivered</c:otherwise>
                                            </c:choose>
                                        </button>
                                    </form>
                                </c:if>

                                <c:if test="${canCancel}">
                                    <form action="${pageContext.request.contextPath}/admin/order" method="post"
                                          onsubmit="return confirm('Cancel this order? Stock will be returned.');">
                                        <input type="hidden" name="action" value="cancel">
                                        <input type="hidden" name="orderId" value="${order.orderId}">
                                        <button type="submit" class="btn btn-outline-danger">Cancel order</button>
                                    </form>
                                </c:if>
                            </div>
                            <small class="text-muted d-block mt-2">
                                Order flow: Placed, Confirmed, Shipped, Delivered. An order can be cancelled until it is shipped.
                            </small>
                        </c:when>
                        <c:otherwise>
                            <p class="text-muted mb-0">No further action is possible for this order.</p>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>

        <!-- Summary, customer, address -->
        <div class="col-lg-4">
            <div class="card shadow-sm mb-3">
                <div class="card-body">
                    <h6 class="card-title">Summary</h6>
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
                    <div class="d-flex justify-content-between fw-bold">
                        <span>Total</span>
                        <span>&#8377;<fmt:formatNumber value="${order.totalAmount}" pattern="#,##0.00"/></span>
                    </div>
                    <hr>
                    <div class="small">
                        Payment: ${order.paymentMethod == 'COD' ? 'Cash on Delivery' : order.paymentMethod}
                        (<c:out value="${order.paymentStatus}"/>)<br>
                        Placed on <fmt:formatDate value="${order.createdAt}" pattern="dd MMM yyyy, hh:mm a"/>
                    </div>
                </div>
            </div>

            <div class="card shadow-sm mb-3">
                <div class="card-body">
                    <h6 class="card-title">Customer</h6>
                    <c:choose>
                        <c:when test="${not empty customer}">
                            <p class="mb-0">
                                <c:out value="${customer.name}"/><br>
                                <c:out value="${customer.email}"/><br>
                                <c:out value="${customer.mobile}"/>
                            </p>
                        </c:when>
                        <c:otherwise>
                            <p class="text-muted mb-0">Customer details not available</p>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <div class="card shadow-sm">
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
                            <p class="text-muted mb-0">Address not available</p>
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
