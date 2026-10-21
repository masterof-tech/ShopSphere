package com.shopsphere.controller;

import com.shopsphere.model.User;
import com.shopsphere.service.WishlistService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/wishlist")
public class WishlistServlet extends HttpServlet {

    private final WishlistService wishlistService = new WishlistService();

    // Wishlist page
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User user = getUser(request);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            request.setAttribute("products", wishlistService.getProducts(user.getUserId()));

            HttpSession session = request.getSession();
            Object message = session.getAttribute("flashMessage");
            if (message != null) {
                request.setAttribute("flashMessage", message);
                request.setAttribute("flashType", session.getAttribute("flashType"));
                session.removeAttribute("flashMessage");
                session.removeAttribute("flashType");
            }

            request.getRequestDispatcher("/wishlist.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Something went wrong");
        }
    }

    // add / remove
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
        HttpSession session = request.getSession();

        try {
            if ("add".equals(action)) {
                String error = wishlistService.add(user.getUserId(), productId);
                session.setAttribute("flashMessage", error == null ? "Added to your wishlist" : error);
                session.setAttribute("flashType", error == null ? "success" : "danger");
            } else if ("remove".equals(action)) {
                wishlistService.remove(user.getUserId(), productId);
                session.setAttribute("flashMessage", "Removed from your wishlist");
                session.setAttribute("flashType", "info");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            session.setAttribute("flashMessage", "Something went wrong. Please try again.");
            session.setAttribute("flashType", "danger");
        }

        // Wapas kahan jaana hai: product page ya wishlist page (sirf ye do options)
        if ("product".equals(request.getParameter("returnTo")) && productId > 0) {
            response.sendRedirect(request.getContextPath() + "/product?id=" + productId);
        } else {
            response.sendRedirect(request.getContextPath() + "/wishlist");
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
