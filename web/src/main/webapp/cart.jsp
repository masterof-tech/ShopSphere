<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Your Cart - ShopSphere</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<jsp:include page="/fragments/navbar.jsp" />

<div class="container py-4">
    <h3 class="mb-4">Your Cart</h3>

    <c:if test="${not empty cartMessage}">
        <div class="alert alert-warning"><c:out value="${cartMessage}"/></div>
    </c:if>

    <c:choose>
        <c:when test="${empty cartItems}">
            <div class="card shadow-sm">
                <div class="card-body text-center py-5">
                    <p class="lead mb-3">Your cart is empty.</p>
                    <a class="btn btn-primary" href="${pageContext.request.contextPath}/products">Browse Products</a>
                </div>
            </div>
        </c:when>

        <c:otherwise>
            <div class="row">
                <div class="col-lg-8 mb-4">
                    <div class="table-responsive">
                        <table class="table align-middle bg-white shadow-sm">
                            <thead class="table-light">
                                <tr>
                                    <th>Product</th>
                                    <th>Price</th>
                                    <th style="width:210px;">Quantity</th>
                                    <th>Subtotal</th>
                                    <th></th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="item" items="${cartItems}">
                                    <tr>
                                        <td>
                                            <a class="text-decoration-none"
                                               href="${pageContext.request.contextPath}/product?id=${item.productId}">
                                                <c:out value="${item.productName}"/>
                                            </a><br>
                                            <small class="text-muted"><c:out value="${item.brand}"/></small>
                                        </td>
                                        <td>&#8377;<fmt:formatNumber value="${item.unitPrice}" pattern="#,##0.00"/></td>
                                        <td>
                                            <form class="d-flex gap-2" action="${pageContext.request.contextPath}/cart" method="post">
                                                <input type="hidden" name="action" value="update">
                                                <input type="hidden" name="productId" value="${item.productId}">
                                                <input type="number" name="quantity" class="form-control form-control-sm"
                                                       value="${item.quantity}" min="1" max="${item.stock}">
                                                <button type="submit" class="btn btn-sm btn-outline-primary">Update</button>
                                            </form>
                                        </td>
                                        <td>&#8377;<fmt:formatNumber value="${item.subtotal}" pattern="#,##0.00"/></td>
                                        <td>
                                            <form action="${pageContext.request.contextPath}/cart" method="post">
                                                <input type="hidden" name="action" value="remove">
                                                <input type="hidden" name="productId" value="${item.productId}">
                                                <button type="submit" class="btn btn-sm btn-outline-danger">Remove</button>
                                            </form>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>

                <div class="col-lg-4">
                    <div class="card shadow-sm">
                        <div class="card-body">
                            <h5 class="card-title">Order Summary</h5>
                            <hr>
                            <div class="d-flex justify-content-between fs-5 fw-bold mb-3">
                                <span>Total</span>
                                <span>&#8377;<fmt:formatNumber value="${cartTotal}" pattern="#,##0.00"/></span>
                            </div>
                            <a class="btn btn-success w-100" href="${pageContext.request.contextPath}/checkout">Proceed to Checkout</a>
                        </div>
                    </div>
                    <a class="btn btn-link mt-2 ps-0" href="${pageContext.request.contextPath}/products">&larr; Continue shopping</a>
                </div>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="/fragments/footer.jsp" />
</body>
</html>
