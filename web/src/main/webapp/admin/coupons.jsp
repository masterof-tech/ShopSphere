<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Manage Coupons - ShopSphere</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<jsp:include page="/fragments/navbar.jsp" />

<div class="container py-4">
    <h3 class="mb-3">Admin Panel</h3>
    <jsp:include page="/fragments/admin-nav.jsp"><jsp:param name="active" value="coupons"/></jsp:include>

    <c:if test="${not empty adminMessage}">
        <div class="alert alert-${adminMessageType}"><c:out value="${adminMessage}"/></div>
    </c:if>

    <div class="row g-4">

        <!-- List -->
        <div class="col-lg-8">
            <div class="table-responsive">
                <table class="table align-middle bg-white shadow-sm">
                    <thead class="table-light">
                        <tr>
                            <th>Code</th>
                            <th>Discount</th>
                            <th>Min order</th>
                            <th>Max discount</th>
                            <th>Expires</th>
                            <th>Used</th>
                            <th>Status</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="row" items="${coupons}">
                            <tr class="${row.status ? '' : 'table-secondary'}">
                                <td><strong><c:out value="${row.code}"/></strong></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${row.discountType == 'PERCENT'}">
                                            <fmt:formatNumber value="${row.discountValue}" maxFractionDigits="2"/>%
                                        </c:when>
                                        <c:otherwise>
                                            &#8377;<fmt:formatNumber value="${row.discountValue}" maxFractionDigits="2"/> off
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>&#8377;<fmt:formatNumber value="${row.minimumOrder}" maxFractionDigits="2"/></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${row.maximumDiscount > 0}">
                                            &#8377;<fmt:formatNumber value="${row.maximumDiscount}" maxFractionDigits="2"/>
                                        </c:when>
                                        <c:otherwise>-</c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${empty row.expiryDate}">Never</c:when>
                                        <c:otherwise>
                                            <c:out value="${row.expiryDate}"/>
                                            <c:if test="${row.expired}"><span class="badge bg-danger">Expired</span></c:if>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>${row.usedCount}</td>
                                <td>
                                    <span class="badge ${row.status ? 'bg-success' : 'bg-secondary'}">${row.status ? 'Active' : 'Disabled'}</span>
                                </td>
                                <td class="text-nowrap">
                                    <a class="btn btn-sm btn-outline-primary"
                                       href="${pageContext.request.contextPath}/admin/coupons?edit=${row.couponId}">Edit</a>

                                    <form class="d-inline" action="${pageContext.request.contextPath}/admin/coupons" method="post">
                                        <input type="hidden" name="action" value="toggle">
                                        <input type="hidden" name="couponId" value="${row.couponId}">
                                        <input type="hidden" name="status" value="${row.status ? 'disable' : 'enable'}">
                                        <button type="submit" class="btn btn-sm ${row.status ? 'btn-outline-danger' : 'btn-outline-success'}">
                                            ${row.status ? 'Disable' : 'Enable'}
                                        </button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
            <small class="text-muted">Each customer can use a coupon only once. A cancelled order gives the coupon back.</small>
        </div>

        <!-- Add / edit form -->
        <div class="col-lg-4">
            <div class="card shadow-sm">
                <div class="card-body">
                    <h5 class="card-title">${not empty editCoupon and editCoupon.couponId > 0 ? 'Edit coupon' : 'Add a coupon'}</h5>

                    <c:if test="${not empty error}">
                        <div class="alert alert-danger"><c:out value="${error}"/></div>
                    </c:if>

                    <form action="${pageContext.request.contextPath}/admin/coupons" method="post">
                        <input type="hidden" name="action" value="save">
                        <input type="hidden" name="couponId" value="${not empty editCoupon ? editCoupon.couponId : 0}">

                        <div class="mb-3">
                            <label class="form-label">Code</label>
                            <input type="text" name="code" class="form-control text-uppercase" maxlength="30"
                                   placeholder="SAVE50" value="<c:out value='${editCoupon.code}'/>" required>
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Discount type</label>
                            <select name="discountType" class="form-select" required>
                                <option value="PERCENT" ${editCoupon.discountType == 'PERCENT' ? 'selected' : ''}>Percentage (%)</option>
                                <option value="FLAT" ${editCoupon.discountType == 'FLAT' ? 'selected' : ''}>Flat amount (&#8377;)</option>
                            </select>
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Discount value</label>
                            <input type="number" name="discountValue" class="form-control" step="0.01" min="0.01"
                                   value="${not empty editCoupon and editCoupon.discountValue > 0 ? editCoupon.discountValue : ''}" required>
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Minimum order (&#8377;)</label>
                            <input type="number" name="minimumOrder" class="form-control" step="0.01" min="0"
                                   placeholder="0" value="${not empty editCoupon and editCoupon.minimumOrder > 0 ? editCoupon.minimumOrder : ''}">
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Maximum discount (&#8377;, percentage coupons only)</label>
                            <input type="number" name="maximumDiscount" class="form-control" step="0.01" min="0"
                                   placeholder="No limit" value="${not empty editCoupon and editCoupon.maximumDiscount > 0 ? editCoupon.maximumDiscount : ''}">
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Expiry date (optional)</label>
                            <input type="date" name="expiryDate" class="form-control"
                                   value="${not empty editCoupon.expiryDate ? editCoupon.expiryDate : ''}">
                        </div>

                        <button type="submit" class="btn btn-primary">Save coupon</button>
                        <c:if test="${not empty editCoupon}">
                            <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/admin/coupons">Cancel</a>
                        </c:if>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/fragments/footer.jsp" />
</body>
</html>
