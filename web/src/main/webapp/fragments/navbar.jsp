<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <div class="container">
        <a class="navbar-brand" href="${pageContext.request.contextPath}/index.jsp">
            Shop<span class="text-info">Sphere</span>
        </a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#mainNav">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="mainNav">
            <ul class="navbar-nav me-auto">
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/products">Products</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/cart">Cart</a>
                </li>
                <c:if test="${not empty sessionScope.user}">
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/orders">My Orders</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/wishlist">Wishlist</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/profile">Profile</a>
                    </li>
                </c:if>
                <c:if test="${sessionScope.role == 'ADMIN'}">
                    <li class="nav-item">
                        <a class="nav-link text-warning" href="${pageContext.request.contextPath}/admin/dashboard">Admin Panel</a>
                    </li>
                </c:if>
            </ul>

            <form class="d-flex me-3 my-2 my-lg-0" action="${pageContext.request.contextPath}/products" method="get">
                <input class="form-control form-control-sm me-2" type="search" name="search"
                       placeholder="Search products" value="<c:out value='${param.search}'/>">
                <button class="btn btn-info btn-sm" type="submit">Search</button>
            </form>

            <div class="d-flex align-items-center text-white">
                <c:choose>
                    <c:when test="${not empty sessionScope.user}">
                        <span class="me-3">
                            Hi, <c:out value="${sessionScope.user.name}"/>
                            <c:if test="${sessionScope.role == 'ADMIN'}">
                                <span class="badge bg-warning text-dark">ADMIN</span>
                            </c:if>
                        </span>
                        <a class="btn btn-outline-light btn-sm" href="${pageContext.request.contextPath}/logout">Logout</a>
                    </c:when>
                    <c:otherwise>
                        <a class="btn btn-outline-light btn-sm me-2" href="${pageContext.request.contextPath}/login">Login</a>
                        <a class="btn btn-info btn-sm" href="${pageContext.request.contextPath}/register">Register</a>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</nav>
