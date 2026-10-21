package com.shopsphere.dao;

import com.shopsphere.model.Product;
import com.shopsphere.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Dashboard ke numbers
public class AdminDAO {

    public int countCustomers() throws SQLException {
        return queryInt("SELECT COUNT(*) FROM users WHERE role = 'CUSTOMER'");
    }

    public int countProducts() throws SQLException {
        return queryInt("SELECT COUNT(*) FROM products");
    }

    public int countOrders() throws SQLException {
        return queryInt("SELECT COUNT(*) FROM orders");
    }

    // Cancel hue orders ko chhodkar total kamai
    public double totalRevenue() throws SQLException {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) FROM orders WHERE order_status <> 'CANCELLED'";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getDouble(1) : 0;
        }
    }

    // Chalu products jinka stock threshold ya usse kam hai
    public List<Product> getLowStockProducts(int threshold) throws SQLException {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT product_id, name, stock FROM products "
                   + "WHERE status = TRUE AND stock <= ? ORDER BY stock, product_id";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, threshold);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Product p = new Product();
                    p.setProductId(rs.getInt("product_id"));
                    p.setName(rs.getString("name"));
                    p.setStock(rs.getInt("stock"));
                    list.add(p);
                }
            }
        }
        return list;
    }

    // Sabse naye orders (customer ke naam ke saath). Har row ek Map hai: JSP mein ${row.orderId} jaise padhte hain
    public List<Map<String, Object>> getRecentOrders(int limit) throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT o.order_id, o.total_amount, o.order_status, o.created_at, u.name "
                   + "FROM orders o JOIN users u ON o.user_id = u.user_id "
                   + "ORDER BY o.order_id DESC LIMIT ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("orderId", rs.getInt("order_id"));
                    row.put("customer", rs.getString("name"));
                    row.put("total", rs.getDouble("total_amount"));
                    row.put("status", rs.getString("order_status"));
                    row.put("createdAt", rs.getTimestamp("created_at"));
                    list.add(row);
                }
            }
        }
        return list;
    }

    private int queryInt(String sql) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }
}
