package com.shopsphere.dao;

import com.shopsphere.model.Review;
import com.shopsphere.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAO {

    private static final String SELECT_WITH_NAME =
            "SELECT r.review_id, r.user_id, r.product_id, r.rating, r.review_text, r.created_at, u.name "
          + "FROM reviews r JOIN users u ON r.user_id = u.user_id ";

    // Ek product ke saare reviews, naya pehle
    public List<Review> getReviewsByProduct(int productId) throws SQLException {
        List<Review> list = new ArrayList<>();
        String sql = SELECT_WITH_NAME + "WHERE r.product_id = ? ORDER BY r.review_id DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapReview(rs));
                }
            }
        }
        return list;
    }

    // Is user ka is product par review (nahi hai to null)
    public Review getReview(int userId, int productId) throws SQLException {
        String sql = SELECT_WITH_NAME + "WHERE r.user_id = ? AND r.product_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapReview(rs);
                }
            }
        }
        return null;
    }

    public void addReview(int userId, int productId, int rating, String text) throws SQLException {
        String sql = "INSERT INTO reviews (user_id, product_id, rating, review_text) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, productId);
            ps.setInt(3, rating);
            ps.setString(4, text);
            ps.executeUpdate();
        }
    }

    public void updateReview(int userId, int productId, int rating, String text) throws SQLException {
        String sql = "UPDATE reviews SET rating = ?, review_text = ?, created_at = CURRENT_TIMESTAMP "
                   + "WHERE user_id = ? AND product_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, rating);
            ps.setString(2, text);
            ps.setInt(3, userId);
            ps.setInt(4, productId);
            ps.executeUpdate();
        }
    }

    public void deleteReview(int userId, int productId) throws SQLException {
        String sql = "DELETE FROM reviews WHERE user_id = ? AND product_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, productId);
            ps.executeUpdate();
        }
    }

    // User ne ye product kharida hai? (cancel kiya hua order ginti mein nahi)
    public boolean hasPurchased(int userId, int productId) throws SQLException {
        String sql = "SELECT 1 FROM orders o JOIN order_items oi ON o.order_id = oi.order_id "
                   + "WHERE o.user_id = ? AND oi.product_id = ? AND o.order_status <> 'CANCELLED' LIMIT 1";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, productId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private Review mapReview(ResultSet rs) throws SQLException {
        Review r = new Review();
        r.setReviewId(rs.getInt("review_id"));
        r.setUserId(rs.getInt("user_id"));
        r.setProductId(rs.getInt("product_id"));
        r.setRating(rs.getInt("rating"));
        r.setReviewText(rs.getString("review_text"));
        r.setCreatedAt(rs.getTimestamp("created_at"));
        r.setUserName(rs.getString("name"));
        return r;
    }
}
