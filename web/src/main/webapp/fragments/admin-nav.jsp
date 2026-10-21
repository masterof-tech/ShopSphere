<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%-- Admin pages ke upar ke tabs. Use: <jsp:include page="/fragments/admin-nav.jsp"><jsp:param name="active" value="products"/></jsp:include> --%>
<ul class="nav nav-pills mb-4">
    <li class="nav-item">
        <a class="nav-link ${param.active == 'dashboard' ? 'active' : ''}"
           href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a>
    </li>
    <li class="nav-item">
        <a class="nav-link ${param.active == 'products' ? 'active' : ''}"
           href="${pageContext.request.contextPath}/admin/products">Products</a>
    </li>
    <li class="nav-item">
        <a class="nav-link ${param.active == 'categories' ? 'active' : ''}"
           href="${pageContext.request.contextPath}/admin/categories">Categories</a>
    </li>
    <li class="nav-item">
        <a class="nav-link ${param.active == 'orders' ? 'active' : ''}"
           href="${pageContext.request.contextPath}/admin/orders">Orders</a>
    </li>
    <li class="nav-item">
        <a class="nav-link ${param.active == 'users' ? 'active' : ''}"
           href="${pageContext.request.contextPath}/admin/users">Users</a>
    </li>
    <li class="nav-item">
        <a class="nav-link ${param.active == 'coupons' ? 'active' : ''}"
           href="${pageContext.request.contextPath}/admin/coupons">Coupons</a>
    </li>
    <li class="nav-item">
        <a class="nav-link ${param.active == 'sales' ? 'active' : ''}"
           href="${pageContext.request.contextPath}/admin/sales">Sales</a>
    </li>
</ul>
