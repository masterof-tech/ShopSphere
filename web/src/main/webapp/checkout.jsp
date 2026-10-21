<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Checkout - ShopSphere</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<jsp:include page="/fragments/navbar.jsp" />

<div class="container py-4">
    <h3 class="mb-4">Checkout</h3>

    <c:if test="${not empty checkoutMessage}">
        <div class="alert alert-${checkoutMessageType}"><c:out value="${checkoutMessage}"/></div>
    </c:if>

    <div class="row">

        <!-- LEFT: address + payment -->
        <div class="col-lg-8 mb-4">

            <!-- 1. Delivery address -->
            <div class="card shadow-sm mb-4">
                <div class="card-body">
                    <h5 class="card-title">1. Delivery address</h5>

                    <c:choose>
                        <c:when test="${empty addresses}">
                            <div class="alert alert-info mb-0">You have no saved address yet. Please add one below.</div>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="a" items="${addresses}" varStatus="s">
                                <div class="border rounded p-3 mb-2">
                                    <div class="form-check">
                                        <input class="form-check-input" type="radio" name="addressId"
                                               id="addr${a.addressId}" value="${a.addressId}"
                                               form="placeOrderForm" ${s.first ? 'checked' : ''} required>
                                        <label class="form-check-label" for="addr${a.addressId}">
                                            <span class="badge bg-secondary"><c:out value="${a.addressType}"/></span><br>
                                            <c:out value="${a.addressLine}"/>,
                                            <c:out value="${a.city}"/>, <c:out value="${a.state}"/>
                                            - <c:out value="${a.pincode}"/>
                                        </label>
                                    </div>
                                </div>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <!-- Add new address -->
            <div class="card shadow-sm mb-4">
                <div class="card-body">
                    <h6 class="card-title">Add a new address</h6>
                    <form action="${pageContext.request.contextPath}/checkout" method="post">
                        <input type="hidden" name="action" value="addAddress">
                        <div class="mb-2">
                            <input type="text" name="addressLine" class="form-control" placeholder="Address line" required>
                        </div>
                        <div class="row g-2 mb-2">
                            <div class="col-md-4">
                                <input type="text" name="city" class="form-control" placeholder="City" required>
                            </div>
                            <div class="col-md-4">
                                <input type="text" name="state" class="form-control" placeholder="State" required>
                            </div>
                            <div class="col-md-4">
                                <input type="text" name="pincode" class="form-control" placeholder="PIN code (6 digits)"
                                       maxlength="6" pattern="\d{6}" required>
                            </div>
                        </div>
                        <div class="row g-2">
                            <div class="col-md-4">
                                <select name="addressType" class="form-select">
                                    <option value="HOME">Home</option>
                                    <option value="WORK">Work</option>
                                    <option value="OTHER">Other</option>
                                </select>
                            </div>
                            <div class="col-md-3 d-grid">
                                <button type="submit" class="btn btn-outline-primary">Save address</button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>

            <!-- 2. Payment -->
            <div class="card shadow-sm">
                <div class="card-body">
                    <h5 class="card-title">2. Payment method</h5>

                    <div class="form-check mb-2">
                        <input class="form-check-input" type="radio" name="paymentMethod" id="payCod"
                               value="COD" form="placeOrderForm" checked>
                        <label class="form-check-label" for="payCod">Cash on Delivery</label>
                    </div>
                    <div class="form-check mb-2">
                        <input class="form-check-input" type="radio" name="paymentMethod" id="payUpi"
                               value="UPI" form="placeOrderForm">
                        <label class="form-check-label" for="payUpi">UPI</label>
                    </div>
                    <div class="form-check mb-3">
                        <input class="form-check-input" type="radio" name="paymentMethod" id="payCard"
                               value="CARD" form="placeOrderForm">
                        <label class="form-check-label" for="payCard">Credit / Debit Card</label>
                    </div>
                    <small class="text-muted">This is a demo project. No real payment is processed.</small>
                </div>
            </div>
        </div>

        <!-- RIGHT: summary -->
        <div class="col-lg-4">
            <div class="card shadow-sm">
                <div class="card-body">
                    <h5 class="card-title">Order Summary</h5>
                    <hr>

                    <c:forEach var="item" items="${cartItems}">
                        <div class="d-flex justify-content-between small mb-1">
                            <span><c:out value="${item.productName}"/> &times; ${item.quantity}</span>
                            <span>&#8377;<fmt:formatNumber value="${item.subtotal}" pattern="#,##0.00"/></span>
                        </div>
                    </c:forEach>

                    <hr>

                    <!-- Coupon -->
                    <form class="d-flex gap-2 mb-2" action="${pageContext.request.contextPath}/checkout" method="get">
                        <input type="text" name="coupon" class="form-control form-control-sm"
                               placeholder="Coupon code" value="<c:out value='${couponCode}'/>">
                        <button type="submit" class="btn btn-sm btn-outline-secondary">Apply</button>
                    </form>
                    <c:if test="${not empty couponError}">
                        <div class="text-danger small mb-2"><c:out value="${couponError}"/></div>
                    </c:if>
                    <c:if test="${couponApplied}">
                        <div class="text-success small mb-2">
                            Coupon <strong><c:out value="${appliedCouponCode}"/></strong> applied.
                            <a href="${pageContext.request.contextPath}/checkout">Remove</a>
                        </div>
                    </c:if>

                    <div class="d-flex justify-content-between">
                        <span>Subtotal</span>
                        <span>&#8377;<fmt:formatNumber value="${subtotal}" pattern="#,##0.00"/></span>
                    </div>
                    <c:if test="${couponApplied}">
                        <div class="d-flex justify-content-between text-success">
                            <span>Coupon discount</span>
                            <span>- &#8377;<fmt:formatNumber value="${couponDiscount}" pattern="#,##0.00"/></span>
                        </div>
                    </c:if>
                    <hr>
                    <div class="d-flex justify-content-between fs-5 fw-bold mb-3">
                        <span>Total payable</span>
                        <span>&#8377;<fmt:formatNumber value="${payable}" pattern="#,##0.00"/></span>
                    </div>

                    <!-- Place order -->
                    <form id="placeOrderForm" action="${pageContext.request.contextPath}/checkout" method="post">
                        <input type="hidden" name="action" value="place">
                        <input type="hidden" name="couponCode" value="<c:out value='${appliedCouponCode}'/>">
                        <button type="submit" class="btn btn-success w-100" ${empty addresses ? 'disabled' : ''}>
                            Place Order
                        </button>
                    </form>
                </div>
            </div>
            <a class="btn btn-link mt-2 ps-0" href="${pageContext.request.contextPath}/cart">&larr; Back to cart</a>
        </div>
    </div>
</div>

<jsp:include page="/fragments/footer.jsp" />
</body>
</html>
