<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Order Placed - ShopSphere</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<jsp:include page="/fragments/navbar.jsp" />

<div class="container py-5">
    <div class="row justify-content-center">
        <div class="col-md-7">
            <div class="card shadow-sm text-center">
                <div class="card-body p-5">
                    <div class="display-4 text-success mb-2">&#10004;</div>
                    <h3>Thank you! Your order has been placed.</h3>
                    <p class="text-muted">Order ID: <strong>#${order.orderId}</strong></p>

                    <table class="table table-borderless text-start mx-auto mt-4" style="max-width:400px;">
                        <tr>
                            <td class="text-muted">Amount</td>
                            <td class="text-end fw-bold">&#8377;<fmt:formatNumber value="${order.totalAmount}" pattern="#,##0.00"/></td>
                        </tr>
                        <c:if test="${order.discount > 0}">
                            <tr>
                                <td class="text-muted">Coupon discount</td>
                                <td class="text-end text-success">&#8377;<fmt:formatNumber value="${order.discount}" pattern="#,##0.00"/></td>
                            </tr>
                        </c:if>
                        <tr>
                            <td class="text-muted">Payment method</td>
                            <td class="text-end">${order.paymentMethod == 'COD' ? 'Cash on Delivery' : order.paymentMethod}</td>
                        </tr>
                        <tr>
                            <td class="text-muted">Payment status</td>
                            <td class="text-end"><c:out value="${order.paymentStatus}"/></td>
                        </tr>
                        <tr>
                            <td class="text-muted">Order status</td>
                            <td class="text-end"><c:out value="${order.orderStatus}"/></td>
                        </tr>
                        <tr>
                            <td class="text-muted">Placed on</td>
                            <td class="text-end"><fmt:formatDate value="${order.createdAt}" pattern="dd MMM yyyy, hh:mm a"/></td>
                        </tr>
                    </table>

                    <a class="btn btn-primary mt-3" href="${pageContext.request.contextPath}/order?id=${order.orderId}">View Order</a>
                    <a class="btn btn-outline-primary mt-3" href="${pageContext.request.contextPath}/products">Continue Shopping</a>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/fragments/footer.jsp" />
</body>
</html>
