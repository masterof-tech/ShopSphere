package com.shopsphere.controller;

import com.shopsphere.model.Order;
import com.shopsphere.service.AdminOrderService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

// /admin/orders?status=SHIPPED -> saare orders (filter ke saath)
// /admin/order?id=5            -> ek order ki details + status badalna
@WebServlet(urlPatterns = {"/admin/orders", "/admin/order"})
public class AdminOrderServlet extends HttpServlet {

    private final AdminOrderService orderService = new AdminOrderService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            moveFlashMessage(request);

            if ("/admin/order".equals(request.getServletPath())) {
                showDetails(request, response);
            } else {
                String status = request.getParameter("status");
                request.setAttribute("orders", orderService.getOrders(status));
                request.setAttribute("statuses", AdminOrderService.STATUSES);
                request.setAttribute("selectedStatus",
                        AdminOrderService.STATUSES.contains(status) ? status : "");
                request.getRequestDispatcher("/admin/orders.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Something went wrong");
        }
    }

    private void showDetails(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {

        Order order = orderService.getOrder(parseInt(request.getParameter("id"), 0));
        if (order == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Order not found");
            return;
        }

        request.setAttribute("order", order);
        request.setAttribute("items", orderService.getItems(order.getOrderId()));
        request.setAttribute("address", orderService.getAddress(order));
        request.setAttribute("customer", orderService.getCustomer(order));
        request.setAttribute("nextStatus", orderService.getNextStatus(order.getOrderStatus()));
        request.setAttribute("canCancel", orderService.canCancel(order.getOrderStatus()));
        request.setAttribute("itemsTotal", Math.round((order.getTotalAmount() + order.getDiscount()) * 100.0) / 100.0);
        request.getRequestDispatcher("/admin/order-details.jsp").forward(request, response);
    }

    // advance (agla status) ya cancel
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        int orderId = parseInt(request.getParameter("orderId"), 0);
        String action = request.getParameter("action");
        HttpSession session = request.getSession();

        try {
            String error = null;
            String success = null;

            if ("advance".equals(action)) {
                error = orderService.advanceStatus(orderId);
                success = "Order #" + orderId + " status updated";
            } else if ("cancel".equals(action)) {
                error = orderService.cancelOrder(orderId);
                success = "Order #" + orderId + " has been cancelled";
            }

            if (success != null) {
                session.setAttribute("adminMessage", error == null ? success : error);
                session.setAttribute("adminMessageType", error == null ? "success" : "danger");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            session.setAttribute("adminMessage", "Something went wrong. Please try again.");
            session.setAttribute("adminMessageType", "danger");
        }

        response.sendRedirect(request.getContextPath() + "/admin/order?id=" + orderId);
    }

    private void moveFlashMessage(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Object message = session.getAttribute("adminMessage");
        if (message != null) {
            request.setAttribute("adminMessage", message);
            request.setAttribute("adminMessageType", session.getAttribute("adminMessageType"));
            session.removeAttribute("adminMessage");
            session.removeAttribute("adminMessageType");
        }
    }

    private int parseInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException | NullPointerException e) {
            return defaultValue;
        }
    }
}
