package com.shopsphere.controller;

import com.shopsphere.model.CartItem;
import com.shopsphere.model.User;
import com.shopsphere.service.CartService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/cart")
public class CartServlet extends HttpServlet {

    private final CartService cartService = new CartService();

    // Cart page dikhao
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User user = getUser(request);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            List<CartItem> items = cartService.getCartItems(user.getUserId());
            request.setAttribute("cartItems", items);
            request.setAttribute("cartTotal", cartService.getTotal(items));

            // Pichhle action ka message (ek baar dikhake hata do)
            HttpSession session = request.getSession();
            Object message = session.getAttribute("cartMessage");
            if (message != null) {
                request.setAttribute("cartMessage", message);
                session.removeAttribute("cartMessage");
            }

            request.getRequestDispatcher("/cart.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Something went wrong");
        }
    }

    // add / update / remove
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
        int productId = parseInt(request.getParameter("productId"), 0);
        String message = null;

        try {
            if ("add".equals(action)) {
                int quantity = parseInt(request.getParameter("quantity"), 1);
                message = cartService.addToCart(user.getUserId(), productId, quantity);

            } else if ("update".equals(action)) {
                int quantity = parseInt(request.getParameter("quantity"), -1);
                if (quantity < 0) {
                    message = "Please enter a valid quantity";
                } else {
                    message = cartService.updateQuantity(user.getUserId(), productId, quantity);
                }

            } else if ("remove".equals(action)) {
                cartService.removeItem(user.getUserId(), productId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            message = "Something went wrong. Please try again.";
        }

        if (message != null) {
            request.getSession().setAttribute("cartMessage", message);
        }

        // POST ke baad redirect (page refresh par dobara submit nahi hoga)
        response.sendRedirect(request.getContextPath() + "/cart");
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
