package com.shopsphere.controller;

import com.shopsphere.model.Product;
import com.shopsphere.model.Review;
import com.shopsphere.model.User;
import com.shopsphere.service.ProductService;
import com.shopsphere.service.ReviewService;
import com.shopsphere.service.WishlistService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

// Ek hi servlet do URLs handle karta hai:
//   /products  -> product list (search, category filter, sort)
//   /product   -> single product details (?id=1) + reviews + wishlist status
@WebServlet(urlPatterns = {"/products", "/product"})
public class ProductServlet extends HttpServlet {

    private final ProductService productService = new ProductService();
    private final ReviewService reviewService = new ReviewService();
    private final WishlistService wishlistService = new WishlistService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            if ("/product".equals(request.getServletPath())) {
                showDetails(request, response);
            } else {
                showList(request, response);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Something went wrong");
        }
    }

    private void showList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {

        int categoryId = parseInt(request.getParameter("category"));
        String keyword = request.getParameter("search");
        String sort = request.getParameter("sort");

        request.setAttribute("products", productService.getProducts(categoryId, keyword, sort));
        request.setAttribute("categories", productService.getCategories());
        request.setAttribute("selectedCategory", categoryId);
        request.setAttribute("keyword", keyword == null ? "" : keyword.trim());
        request.setAttribute("sort", sort == null ? "" : sort);

        request.getRequestDispatcher("/products.jsp").forward(request, response);
    }

    private void showDetails(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {

        Product product = productService.getProduct(parseInt(request.getParameter("id")));

        if (product == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Product not found");
            return;
        }
        int productId = product.getProductId();

        request.setAttribute("product", product);
        request.setAttribute("category", productService.getCategory(product.getCategoryId()));

        // Reviews sabko dikhte hain (login zaroori nahi)
        List<Review> reviews = reviewService.getReviews(productId);
        request.setAttribute("reviews", reviews);
        request.setAttribute("reviewCount", reviews.size());
        request.setAttribute("avgRating", reviewService.average(reviews));

        HttpSession session = request.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("user");

        // Wishlist aur review ka option sirf logged-in user ke liye
        if (user != null) {
            request.setAttribute("inWishlist", wishlistService.isInWishlist(user.getUserId(), productId));
            request.setAttribute("canReview", reviewService.canReview(user.getUserId(), productId));
            request.setAttribute("myReview", reviewService.getMyReview(user.getUserId(), productId));
        }

        // Pichhle action ka message (ek baar dikhake hata do)
        if (session != null) {
            Object message = session.getAttribute("flashMessage");
            if (message != null) {
                request.setAttribute("flashMessage", message);
                request.setAttribute("flashType", session.getAttribute("flashType"));
                session.removeAttribute("flashMessage");
                session.removeAttribute("flashType");
            }
        }

        request.getRequestDispatcher("/product-details.jsp").forward(request, response);
    }

    // "abc" ya khali aaye to 0 return karo (crash nahi hona chahiye)
    private int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
