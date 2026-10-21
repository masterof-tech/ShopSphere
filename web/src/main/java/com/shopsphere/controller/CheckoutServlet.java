package com.shopsphere.controller;

import com.shopsphere.model.CartItem;
import com.shopsphere.model.Order;
import com.shopsphere.model.User;
import com.shopsphere.service.CartService;
import com.shopsphere.service.CheckoutService;
import com.shopsphere.service.CheckoutService.CouponCheck;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

// /checkout       -> checkout page (GET), address add + order place (POST)
// /order-success  -> order place hone ke baad ka page
@WebServlet(urlPatterns = {"/checkout", "/order-success"})
public class CheckoutServlet extends HttpServlet {

    private final CheckoutService checkoutService = new CheckoutService();
    private final CartService cartService = new CartService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User user = getUser(request);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            if ("/order-success".equals(request.getServletPath())) {
                showSuccess(request, response, user);
            } else {
                showCheckout(request, response, user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Something went wrong");
        }
    }

    private void showCheckout(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException, SQLException {

        List<CartItem> items = cartService.getCartItems(user.getUserId());
        if (items.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        double subtotal = Math.round(cartService.getTotal(items) * 100.0) / 100.0;

        // Coupon preview (Apply button se ?coupon=CODE aata hai)
        String couponCode = request.getParameter("coupon");
        CouponCheck check = checkoutService.applyCoupon(couponCode, subtotal, user.getUserId());
        double discount = check.getDiscount();

        request.setAttribute("cartItems", items);
        request.setAttribute("subtotal", subtotal);
        request.setAttribute("addresses", checkoutService.getAddresses(user.getUserId()));
        request.setAttribute("couponCode", couponCode == null ? "" : couponCode.trim());
        request.setAttribute("couponError", check.getError());
        request.setAttribute("couponApplied", check.getCoupon() != null);
        request.setAttribute("appliedCouponCode", check.getCoupon() != null ? check.getCoupon().getCode() : "");
        request.setAttribute("couponDiscount", discount);
        request.setAttribute("payable", Math.round((subtotal - discount) * 100.0) / 100.0);

        // Pichhle action ka message (ek baar dikhake hata do)
        HttpSession session = request.getSession();
        Object message = session.getAttribute("checkoutMessage");
        if (message != null) {
            request.setAttribute("checkoutMessage", message);
            request.setAttribute("checkoutMessageType", session.getAttribute("checkoutMessageType"));
            session.removeAttribute("checkoutMessage");
            session.removeAttribute("checkoutMessageType");
        }

        request.getRequestDispatcher("/checkout.jsp").forward(request, response);
    }

    private void showSuccess(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException, SQLException {

        Order order = checkoutService.getOrder(parseInt(request.getParameter("orderId"), 0), user.getUserId());
        if (order == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Order not found");
            return;
        }
        request.setAttribute("order", order);
        request.getRequestDispatcher("/order-success.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        User user = getUser(request);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");
        HttpSession session = request.getSession();

        try {
            if ("addAddress".equals(action)) {
                String error = checkoutService.addAddress(user.getUserId(),
                        request.getParameter("addressLine"),
                        request.getParameter("city"),
                        request.getParameter("state"),
                        request.getParameter("pincode"),
                        request.getParameter("addressType"));

                session.setAttribute("checkoutMessage", error == null ? "Address added successfully" : error);
                session.setAttribute("checkoutMessageType", error == null ? "success" : "danger");

            } else if ("place".equals(action)) {
                try {
                    int orderId = checkoutService.placeOrder(user.getUserId(),
                            parseInt(request.getParameter("addressId"), 0),
                            request.getParameter("paymentMethod"),
                            request.getParameter("couponCode"));

                    response.sendRedirect(request.getContextPath() + "/order-success?orderId=" + orderId);
                    return;

                } catch (IllegalStateException e) {
                    session.setAttribute("checkoutMessage", e.getMessage());
                    session.setAttribute("checkoutMessageType", "danger");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            session.setAttribute("checkoutMessage", "Something went wrong. Please try again.");
            session.setAttribute("checkoutMessageType", "danger");
        }

        response.sendRedirect(request.getContextPath() + "/checkout");
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
