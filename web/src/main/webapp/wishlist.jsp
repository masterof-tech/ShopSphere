<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>My Wishlist - ShopSphere</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<jsp:include page="/fragments/navbar.jsp" />

<div class="container py-4">
    <h3 class="mb-4">My Wishlist</h3>

    <c:if test="${not empty flashMessage}">
        <div class="alert alert-${flashType}"><c:out value="${flashMessage}"/></div>
    </c:if>

    <c:choose>
        <c:when test="${empty products}">
            <div class="card shadow-sm">
                <div class="card-body text-center py-5">
                    <p class="lead mb-3">Your wishlist is empty.</p>
                    <a class="btn btn-primary" href="${pageContext.request.contextPath}/products">Browse Products</a>
                </div>
            </div>
        </c:when>

        <c:otherwise>
            <div class="row row-cols-1 row-cols-sm-2 row-cols-lg-3 g-4">
                <c:forEach var="p" items="${products}">
                    <div class="col">
                        <div class="card h-100 shadow-sm">

                            <img src="${pageContext.request.contextPath}/${p.imageUrl}" class="card-img-top"
                                 alt="<c:out value='${p.name}'/>" style="height:180px; object-fit:cover;"
                                 onerror="this.style.display='none'; this.nextElementSibling.style.display='flex';">
                            <div class="bg-secondary-subtle text-secondary align-items-center justify-content-center"
                                 style="height:180px; display:none;">
                                No image
                            </div>

                            <div class="card-body d-flex flex-column">
                                <small class="text-muted"><c:out value="${p.brand}"/></small>
                                <h6 class="card-title"><c:out value="${p.name}"/></h6>

                                <div class="mb-2">
                                    <span class="fw-bold fs-5">
                                        &#8377;<fmt:formatNumber value="${p.finalPrice}" pattern="#,##0.00"/>
                                    </span>
                                    <c:if test="${p.discount > 0}">
                                        <small class="text-muted text-decoration-line-through ms-1">
                                            &#8377;<fmt:formatNumber value="${p.price}" pattern="#,##0.00"/>
                                        </small>
                                    </c:if>
                                </div>

                                <c:choose>
                                    <c:when test="${p.stock > 0}">
                                        <small class="text-success mb-3">In stock</small>
                                    </c:when>
                                    <c:otherwise>
                                        <small class="text-danger mb-3">Out of stock</small>
                                    </c:otherwise>
                                </c:choose>

                                <div class="mt-auto d-grid gap-2">
                                    <a href="${pageContext.request.contextPath}/product?id=${p.productId}"
                                       class="btn btn-outline-primary btn-sm">View Details</a>

                                    <c:if test="${p.stock > 0}">
                                        <form action="${pageContext.request.contextPath}/cart" method="post" class="d-grid">
                                            <input type="hidden" name="action" value="add">
                                            <input type="hidden" name="productId" value="${p.productId}">
                                            <input type="hidden" name="quantity" value="1">
                                            <button type="submit" class="btn btn-primary btn-sm">Add to Cart</button>
                                        </form>
                                    </c:if>

                                    <form action="${pageContext.request.contextPath}/wishlist" method="post" class="d-grid">
                                        <input type="hidden" name="action" value="remove">
                                        <input type="hidden" name="productId" value="${p.productId}">
                                        <button type="submit" class="btn btn-outline-danger btn-sm">Remove</button>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="/fragments/footer.jsp" />
</body>
</html>
