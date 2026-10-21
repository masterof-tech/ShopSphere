package com.shopsphere.controller;

import com.shopsphere.model.Order;
import com.shopsphere.model.OrderItem;
import com.shopsphere.model.User;
import com.shopsphere.service.OrderService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

// /orders       -> My Orders list
// /order?id=5   -> ek order ki details (POST se cancel)
@WebServlet(urlPatterns = {"/orders", "/order"})
public class OrderServlet extends HttpServlet {

    private final OrderService orderService = new OrderService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User user = getUser(request);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            moveFlashMessage(request);

            if ("/order".equals(request.getServletPath())) {
                showDetails(request, response, user);
            } else {
                request.setAttribute("orders", orderService.getOrders(user.getUserId()));
                request.getRequestDispatcher("/orders.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Something went wrong");
        }
    }

    private void showDetails(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException, SQLException {

        Order order = orderService.getOrder(parseInt(request.getParameter("id"), 0), user.getUserId());
        if (order == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Order not found");
            return;
        }

        List<OrderItem> items = orderService.getItems(order.getOrderId());
        request.setAttribute("order", order);
        request.setAttribute("items", items);
        request.setAttribute("address", orderService.getAddress(order));
        // coupon se pehle ka amount
        request.setAttribute("itemsTotal", Math.round((order.getTotalAmount() + order.getDiscount()) * 100.0) / 100.0);
        request.getRequestDispatcher("/order-details.jsp").forward(request, response);
    }

    // Cancel
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        User user = getUser(request);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int orderId = parseInt(request.getParameter("orderId"), 0);
        HttpSession session = request.getSession();

        if ("cancel".equals(request.getParameter("action"))) {
            try {
                String error = orderService.cancelOrder(orderId, user.getUserId());
                session.setAttribute("orderMessage", error == null
                        ? "Order #" + orderId + " has been cancelled" : error);
                session.setAttribute("orderMessageType", error == null ? "success" : "danger");
            } catch (SQLException e) {
                e.printStackTrace();
                session.setAttribute("orderMessage", "Something went wrong. Please try again.");
                session.setAttribute("orderMessageType", "danger");
            }
        }

        response.sendRedirect(request.getContextPath() + "/order?id=" + orderId);
    }

    // Pichhle action ka message ek baar dikhake hata do
    private void moveFlashMessage(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Object message = session.getAttribute("orderMessage");
        if (message != null) {
            request.setAttribute("orderMessage", message);
            request.setAttribute("orderMessageType", session.getAttribute("orderMessageType"));
            session.removeAttribute("orderMessage");
            session.removeAttribute("orderMessageType");
        }
    }

    private User getUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session == null ? null : (User) session.getAttribute("user");
    }

    private int parseInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
