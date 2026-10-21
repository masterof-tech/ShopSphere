package com.shopsphere.service;

import com.shopsphere.dao.ProductDAO;
import com.shopsphere.dao.ReviewDAO;
import com.shopsphere.model.Product;
import com.shopsphere.model.Review;

import java.sql.SQLException;
import java.util.List;

public class ReviewService {

    private final ReviewDAO reviewDAO = new ReviewDAO();
    private final ProductDAO productDAO = new ProductDAO();

    public List<Review> getReviews(int productId) throws SQLException {
        return reviewDAO.getReviewsByProduct(productId);
    }

    // Reviews ki average rating (koi review nahi to 0)
    public double average(List<Review> reviews) {
        if (reviews.isEmpty()) {
            return 0;
        }
        int sum = 0;
        for (Review r : reviews) {
            sum += r.getRating();
        }
        return (double) sum / reviews.size();
    }

    public Review getMyReview(int userId, int productId) throws SQLException {
        return reviewDAO.getReview(userId, productId);
    }

    // Sirf wahi review likh sakta hai jisne ye product kharida ho
    public boolean canReview(int userId, int productId) throws SQLException {
        return reviewDAO.hasPurchased(userId, productId);
    }

    // Sab theek ho to null, warna error message. Pehle se review hai to update ho jata hai.
    public String saveReview(int userId, int productId, int rating, String text) throws SQLException {
        if (rating < 1 || rating > 5) {
            return "Please select a rating from 1 to 5";
        }
        text = text == null ? "" : text.trim();
        if (text.length() > 500) {
            return "Review must be 500 characters or less";
        }

        Product p = productDAO.getProductById(productId);
        if (p == null || !p.isStatus()) {
            return "Product not found";
        }
        if (!reviewDAO.hasPurchased(userId, productId)) {
            return "Only customers who have bought this product can review it";
        }

        if (reviewDAO.getReview(userId, productId) == null) {
            reviewDAO.addReview(userId, productId, rating, text);
        } else {
            reviewDAO.updateReview(userId, productId, rating, text);
        }
        return null;
    }

    public void deleteReview(int userId, int productId) throws SQLException {
        reviewDAO.deleteReview(userId, productId);
    }
}
