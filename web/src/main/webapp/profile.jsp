<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>My Profile - ShopSphere</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<jsp:include page="/fragments/navbar.jsp" />

<div class="container py-4">
    <h3 class="mb-4">My Profile</h3>

    <c:if test="${not empty profileMessage}">
        <div class="alert alert-${profileMessageType}"><c:out value="${profileMessage}"/></div>
    </c:if>

    <div class="row g-4">

        <!-- Left: account details + password -->
        <div class="col-lg-5">

            <div class="card shadow-sm mb-4">
                <div class="card-body">
                    <h5 class="card-title">Account details</h5>
                    <form action="${pageContext.request.contextPath}/profile" method="post">
                        <input type="hidden" name="action" value="updateProfile">

                        <div class="mb-3">
                            <label class="form-label">Full name</label>
                            <input type="text" name="name" class="form-control" maxlength="100"
                                   value="<c:out value='${profile.name}'/>" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Email</label>
                            <input type="email" class="form-control" value="<c:out value='${profile.email}'/>" disabled>
                            <div class="form-text">Email cannot be changed.</div>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Mobile (10 digits)</label>
                            <input type="text" name="mobile" class="form-control" maxlength="10" pattern="\d{10}"
                                   value="<c:out value='${profile.mobile}'/>" required>
                        </div>
                        <button type="submit" class="btn btn-primary">Save changes</button>
                        <small class="text-muted ms-2">
                            Member since <fmt:formatDate value="${profile.createdAt}" pattern="dd MMM yyyy"/>
                        </small>
                    </form>
                </div>
            </div>

            <div class="card shadow-sm">
                <div class="card-body">
                    <h5 class="card-title">Change password</h5>
                    <form action="${pageContext.request.contextPath}/profile" method="post">
                        <input type="hidden" name="action" value="changePassword">

                        <div class="mb-3">
                            <label class="form-label">Current password</label>
                            <input type="password" name="currentPassword" class="form-control" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">New password (minimum 6 characters)</label>
                            <input type="password" name="newPassword" class="form-control" minlength="6" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Re-enter new password</label>
                            <input type="password" name="confirmPassword" class="form-control" minlength="6" required>
                        </div>
                        <button type="submit" class="btn btn-outline-primary">Change password</button>
                    </form>
                </div>
            </div>
        </div>

        <!-- Right: addresses -->
        <div class="col-lg-7">
            <div class="card shadow-sm mb-4">
                <div class="card-body">
                    <h5 class="card-title">My addresses</h5>

                    <c:choose>
                        <c:when test="${empty addresses}">
                            <p class="text-muted mb-0">You have no saved addresses yet.</p>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="a" items="${addresses}">
                                <div class="border rounded p-3 mb-2 d-flex justify-content-between align-items-start">
                                    <div>
                                        <span class="badge bg-secondary"><c:out value="${a.addressType}"/></span><br>
                                        <c:out value="${a.addressLine}"/>,
                                        <c:out value="${a.city}"/>, <c:out value="${a.state}"/>
                                        - <c:out value="${a.pincode}"/>
                                    </div>
                                    <form action="${pageContext.request.contextPath}/profile" method="post"
                                          onsubmit="return confirm('Delete this address?');">
                                        <input type="hidden" name="action" value="deleteAddress">
                                        <input type="hidden" name="addressId" value="${a.addressId}">
                                        <button type="submit" class="btn btn-sm btn-outline-danger">Delete</button>
                                    </form>
                                </div>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <div class="card shadow-sm">
                <div class="card-body">
                    <h6 class="card-title">Add a new address</h6>
                    <form action="${pageContext.request.contextPath}/profile" method="post">
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
                            <div class="col-md-4 d-grid">
                                <button type="submit" class="btn btn-outline-primary">Save address</button>
                            </div>
                        </div>
                    </form>
                    <small class="text-muted d-block mt-2">An address used in an order cannot be deleted.</small>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/fragments/footer.jsp" />
</body>
</html>
