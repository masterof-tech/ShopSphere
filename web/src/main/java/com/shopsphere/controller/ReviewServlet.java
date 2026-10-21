package com.shopsphere.controller;

import com.shopsphere.model.User;
import com.shopsphere.service.ReviewService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/review")
public class ReviewServlet extends HttpServlet {

    private final ReviewService reviewService = new ReviewService();

    // Sirf POST: save ya delete
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");
        int productId = parseInt(request.getParameter("productId"), 0);

        try {
            if ("save".equals(action)) {
                String error = reviewService.saveReview(user.getUserId(), productId,
                        parseInt(request.getParameter("rating"), 0),
                        request.getParameter("reviewText"));
                session.setAttribute("flashMessage", error == null ? "Thank you! Your review has been saved" : error);
                session.setAttribute("flashType", error == null ? "success" : "danger");

            } else if ("delete".equals(action)) {
                reviewService.deleteReview(user.getUserId(), productId);
                session.setAttribute("flashMessage", "Your review has been deleted");
                session.setAttribute("flashType", "info");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            session.setAttribute("flashMessage", "Something went wrong. Please try again.");
            session.setAttribute("flashType", "danger");
        }

        response.sendRedirect(request.getContextPath() + "/product?id=" + productId + "#reviews");
    }

    // GET se koi kaam nahi, seedha products par bhej do
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/products");
    }

    private int parseInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
