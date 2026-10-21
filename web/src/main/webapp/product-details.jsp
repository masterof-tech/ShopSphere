<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><c:out value="${product.name}"/> - ShopSphere</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<jsp:include page="/fragments/navbar.jsp" />

<div class="container py-4">
    <a href="${pageContext.request.contextPath}/products" class="text-decoration-none">&larr; Back to products</a>

    <c:if test="${not empty flashMessage}">
        <div class="alert alert-${flashType} mt-3"><c:out value="${flashMessage}"/></div>
    </c:if>

    <div class="card shadow-sm mt-3">
        <div class="card-body p-4">
            <div class="row">

                <div class="col-md-5 mb-3">
                    <img src="${pageContext.request.contextPath}/${product.imageUrl}" class="img-fluid rounded"
                         alt="<c:out value='${product.name}'/>"
                         onerror="this.style.display='none'; this.nextElementSibling.style.display='flex';">
                    <div class="bg-secondary-subtle text-secondary align-items-center justify-content-center rounded"
                         style="height:300px; display:none;">
                        No image
                    </div>
                </div>

                <div class="col-md-7">
                    <small class="text-muted">
                        <c:out value="${product.brand}"/>
                        <c:if test="${not empty category}"> | <c:out value="${category.name}"/></c:if>
                    </small>
                    <h2><c:out value="${product.name}"/></h2>

                    <!-- Rating summary -->
                    <div class="mb-2">
                        <c:choose>
                            <c:when test="${reviewCount > 0}">
                                <span class="text-warning fs-5">
                                    <c:forEach var="i" begin="1" end="5">
                                        <c:choose>
                                            <c:when test="${i <= avgRating + 0.5}">&#9733;</c:when>
                                            <c:otherwise>&#9734;</c:otherwise>
                                        </c:choose>
                                    </c:forEach>
                                </span>
                                <strong><fmt:formatNumber value="${avgRating}" minFractionDigits="1" maxFractionDigits="1"/></strong>
                                <a href="#reviews" class="text-muted small text-decoration-none">(${reviewCount} reviews)</a>
                            </c:when>
                            <c:otherwise>
                                <a href="#reviews" class="text-muted small text-decoration-none">No reviews yet</a>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <div class="my-3">
                        <span class="display-6 fw-bold">
                            &#8377;<fmt:formatNumber value="${product.finalPrice}" pattern="#,##0.00"/>
                        </span>
                        <c:if test="${product.discount > 0}">
                            <span class="text-muted text-decoration-line-through ms-2">
                                &#8377;<fmt:formatNumber value="${product.price}" pattern="#,##0.00"/>
                            </span>
                            <span class="badge bg-success ms-2">
                                <fmt:formatNumber value="${product.discount}" maxFractionDigits="0"/>% OFF
                            </span>
                        </c:if>
                    </div>

                    <c:choose>
                        <c:when test="${product.stock > 0}">
                            <p class="text-success">In stock (<c:out value="${product.stock}"/> available)</p>
                        </c:when>
                        <c:otherwise>
                            <p class="text-danger">Out of stock</p>
                        </c:otherwise>
                    </c:choose>

                    <p><c:out value="${product.description}"/></p>

                    <!-- Add to cart -->
                    <c:choose>
                        <c:when test="${product.stock > 0}">
                            <form class="d-flex align-items-center gap-2" action="${pageContext.request.contextPath}/cart" method="post">
                                <input type="hidden" name="action" value="add">
                                <input type="hidden" name="productId" value="${product.productId}">
                                <input type="number" name="quantity" class="form-control" style="width:90px;"
                                       value="1" min="1" max="${product.stock}">
                                <button type="submit" class="btn btn-primary">Add to Cart</button>
                            </form>
                        </c:when>
                        <c:otherwise>
                            <button class="btn btn-secondary" disabled>Out of stock</button>
                        </c:otherwise>
                    </c:choose>

                    <!-- Wishlist -->
                    <div class="mt-3">
                        <c:choose>
                            <c:when test="${empty sessionScope.user}">
                                <a class="btn btn-outline-secondary btn-sm"
                                   href="${pageContext.request.contextPath}/login">Log in to add to wishlist</a>
                            </c:when>
                            <c:when test="${inWishlist}">
                                <form class="d-inline" action="${pageContext.request.contextPath}/wishlist" method="post">
                                    <input type="hidden" name="action" value="remove">
                                    <input type="hidden" name="productId" value="${product.productId}">
                                    <input type="hidden" name="returnTo" value="product">
                                    <button type="submit" class="btn btn-outline-danger btn-sm">&#9829; Remove from Wishlist</button>
                                </form>
                            </c:when>
                            <c:otherwise>
                                <form class="d-inline" action="${pageContext.request.contextPath}/wishlist" method="post">
                                    <input type="hidden" name="action" value="add">
                                    <input type="hidden" name="productId" value="${product.productId}">
                                    <input type="hidden" name="returnTo" value="product">
                                    <button type="submit" class="btn btn-outline-secondary btn-sm">&#9825; Add to Wishlist</button>
                                </form>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Reviews -->
    <div class="card shadow-sm mt-4" id="reviews">
        <div class="card-body p-4">
            <h4 class="mb-3">Customer Reviews (${reviewCount})</h4>

            <!-- Write a review -->
            <c:choose>
                <c:when test="${empty sessionScope.user}">
                    <p class="text-muted">
                        <a href="${pageContext.request.contextPath}/login">Log in</a> to write a review.
                        Only customers who bought this product can review it.
                    </p>
                </c:when>

                <c:when test="${canReview}">
                    <form class="border rounded p-3 mb-4 bg-light" action="${pageContext.request.contextPath}/review" method="post">
                        <h6><c:out value="${empty myReview ? 'Write a review' : 'Edit your review'}"/></h6>
                        <input type="hidden" name="action" value="save">
                        <input type="hidden" name="productId" value="${product.productId}">

                        <div class="mb-2">
                            <select name="rating" class="form-select" style="max-width:220px;" required>
                                <option value="">Select rating</option>
                                <c:forEach var="i" begin="1" end="5">
                                    <option value="${i}" ${not empty myReview and myReview.rating == i ? 'selected' : ''}>${i} star${i > 1 ? 's' : ''}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="mb-2">
                            <textarea name="reviewText" class="form-control" rows="3" maxlength="500"
                                      placeholder="Share your experience (optional, max 500 characters)"><c:out value="${myReview.reviewText}"/></textarea>
                        </div>
                        <button type="submit" class="btn btn-primary btn-sm">Save review</button>
                    </form>

                    <c:if test="${not empty myReview}">
                        <form class="mb-4" action="${pageContext.request.contextPath}/review" method="post"
                              onsubmit="return confirm('Delete your review?');">
                            <input type="hidden" name="action" value="delete">
                            <input type="hidden" name="productId" value="${product.productId}">
                            <button type="submit" class="btn btn-outline-danger btn-sm">Delete my review</button>
                        </form>
                    </c:if>
                </c:when>

                <c:otherwise>
                    <p class="text-muted">Only customers who have bought this product can write a review.</p>
                </c:otherwise>
            </c:choose>

            <!-- Review list -->
            <c:choose>
                <c:when test="${empty reviews}">
                    <p class="text-muted mb-0">No reviews yet. Be the first to review this product.</p>
                </c:when>
                <c:otherwise>
                    <c:forEach var="r" items="${reviews}">
                        <div class="border-top pt-3 mt-3">
                            <div class="d-flex justify-content-between">
                                <strong><c:out value="${r.userName}"/></strong>
                                <small class="text-muted"><fmt:formatDate value="${r.createdAt}" pattern="dd MMM yyyy"/></small>
                            </div>
                            <div class="text-warning">
                                <c:forEach var="i" begin="1" end="5">
                                    <c:choose>
                                        <c:when test="${i <= r.rating}">&#9733;</c:when>
                                        <c:otherwise>&#9734;</c:otherwise>
                                    </c:choose>
                                </c:forEach>
                            </div>
                            <c:if test="${not empty r.reviewText}">
                                <p class="mb-0 mt-1"><c:out value="${r.reviewText}"/></p>
                            </c:if>
                        </div>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<jsp:include page="/fragments/footer.jsp" />
</body>
</html>
