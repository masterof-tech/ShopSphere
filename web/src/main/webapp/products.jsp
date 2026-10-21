<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Products - ShopSphere</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<jsp:include page="/fragments/navbar.jsp" />

<div class="container py-4">
    <div class="row">

        <!-- Category sidebar -->
        <div class="col-md-3 mb-4">
            <div class="list-group shadow-sm">
                <a href="${pageContext.request.contextPath}/products"
                   class="list-group-item list-group-item-action ${selectedCategory == 0 ? 'active' : ''}">
                    All Categories
                </a>
                <c:forEach var="cat" items="${categories}">
                    <a href="${pageContext.request.contextPath}/products?category=${cat.categoryId}"
                       class="list-group-item list-group-item-action ${selectedCategory == cat.categoryId ? 'active' : ''}">
                        <c:out value="${cat.name}"/>
                    </a>
                </c:forEach>
            </div>
        </div>

        <!-- Products -->
        <div class="col-md-9">

            <!-- Search + sort -->
            <form class="row g-2 mb-4" action="${pageContext.request.contextPath}/products" method="get">
                <input type="hidden" name="category" value="${selectedCategory}">
                <div class="col-sm-6">
                    <input type="text" name="search" class="form-control" placeholder="Search by name or brand"
                           value="<c:out value='${keyword}'/>">
                </div>
                <div class="col-sm-4">
                    <select name="sort" class="form-select">
                        <option value="">Sort by</option>
                        <option value="price_asc"  ${sort == 'price_asc'  ? 'selected' : ''}>Price: Low to High</option>
                        <option value="price_desc" ${sort == 'price_desc' ? 'selected' : ''}>Price: High to Low</option>
                        <option value="name"       ${sort == 'name'       ? 'selected' : ''}>Name: A to Z</option>
                    </select>
                </div>
                <div class="col-sm-2 d-grid">
                    <button type="submit" class="btn btn-primary">Apply</button>
                </div>
            </form>

            <c:choose>
                <c:when test="${empty products}">
                    <div class="alert alert-warning">No products found.</div>
                </c:when>
                <c:otherwise>
                    <div class="row row-cols-1 row-cols-sm-2 row-cols-lg-3 g-4">
                        <c:forEach var="p" items="${products}">
                            <div class="col">
                                <div class="card h-100 shadow-sm">

                                    <!-- Image (placeholder is shown if the image file is missing) -->
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
                                                <span class="badge bg-success ms-1">
                                                    <fmt:formatNumber value="${p.discount}" maxFractionDigits="0"/>% OFF
                                                </span>
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

                                        <a href="${pageContext.request.contextPath}/product?id=${p.productId}"
                                           class="btn btn-outline-primary btn-sm mt-auto">View Details</a>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<jsp:include page="/fragments/footer.jsp" />
</body>
</html>
