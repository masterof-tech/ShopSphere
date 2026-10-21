<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${product.productId > 0 ? 'Edit' : 'Add'} Product - ShopSphere</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<jsp:include page="/fragments/navbar.jsp" />

<div class="container py-4">
    <h3 class="mb-3">Admin Panel</h3>
    <jsp:include page="/fragments/admin-nav.jsp"><jsp:param name="active" value="products"/></jsp:include>

    <div class="row">
        <div class="col-lg-8">
            <div class="card shadow-sm">
                <div class="card-body p-4">
                    <h5 class="mb-3">${product.productId > 0 ? 'Edit product' : 'Add a new product'}</h5>

                    <c:if test="${not empty error}">
                        <div class="alert alert-danger"><c:out value="${error}"/></div>
                    </c:if>

                    <form action="${pageContext.request.contextPath}/admin/products" method="post">
                        <input type="hidden" name="action" value="save">
                        <input type="hidden" name="productId" value="${product.productId}">

                        <div class="mb-3">
                            <label class="form-label">Product name</label>
                            <input type="text" name="name" class="form-control" maxlength="150"
                                   value="<c:out value='${product.name}'/>" required>
                        </div>

                        <div class="row g-3 mb-3">
                            <div class="col-md-6">
                                <label class="form-label">Brand</label>
                                <input type="text" name="brand" class="form-control" maxlength="80"
                                       value="<c:out value='${product.brand}'/>">
                            </div>
                            <div class="col-md-6">
                                <label class="form-label">Category</label>
                                <select name="categoryId" class="form-select" required>
                                    <option value="">Select category</option>
                                    <c:forEach var="cat" items="${categories}">
                                        <option value="${cat.categoryId}" ${product.categoryId == cat.categoryId ? 'selected' : ''}>
                                            <c:out value="${cat.name}"/><c:if test="${not cat.status}"> (disabled)</c:if>
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>

                        <div class="row g-3 mb-3">
                            <div class="col-md-4">
                                <label class="form-label">Price (&#8377;)</label>
                                <input type="number" name="price" class="form-control" step="0.01" min="0.01"
                                       value="${product.price > 0 ? product.price : ''}" required>
                            </div>
                            <div class="col-md-4">
                                <label class="form-label">Discount (%)</label>
                                <input type="number" name="discount" class="form-control" step="0.01" min="0" max="100"
                                       value="${product.discount >= 0 ? product.discount : ''}">
                            </div>
                            <div class="col-md-4">
                                <label class="form-label">Stock</label>
                                <input type="number" name="stock" class="form-control" step="1" min="0"
                                       value="${product.stock >= 0 ? product.stock : ''}" required>
                            </div>
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Image path</label>
                            <input type="text" name="imageUrl" class="form-control" maxlength="255"
                                   placeholder="images/phone.jpg" value="<c:out value='${product.imageUrl}'/>">
                            <div class="form-text">Path of the image inside the website, for example images/phone.jpg. Leave empty for no image.</div>
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Description</label>
                            <textarea name="description" class="form-control" rows="4"><c:out value="${product.description}"/></textarea>
                        </div>

                        <button type="submit" class="btn btn-primary">Save product</button>
                        <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/admin/products">Cancel</a>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/fragments/footer.jsp" />
</body>
</html>
