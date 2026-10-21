package com.shopsphere.dao;

import com.shopsphere.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Admin ke sales summary ke numbers. Cancel hue orders kamai mein ginti nahi hote.
public class SalesDAO {

    // validOrders, revenue, cancelledOrders
    public Map<String, Object> getSummary() throws SQLException {
        String sql = "SELECT "
                   + "SUM(CASE WHEN order_status <> 'CANCELLED' THEN 1 ELSE 0 END) AS valid_orders, "
                   + "COALESCE(SUM(CASE WHEN order_status <> 'CANCELLED' THEN total_amount ELSE 0 END), 0) AS revenue, "
                   + "SUM(CASE WHEN order_status = 'CANCELLED' THEN 1 ELSE 0 END) AS cancelled_orders "
                   + "FROM orders";
        Map<String, Object> summary = new LinkedHashMap<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                summary.put("validOrders", rs.getInt("valid_orders"));        // NULL ho to 0
                summary.put("revenue", rs.getDouble("revenue"));
                summary.put("cancelledOrders", rs.getInt("cancelled_orders"));
            }
        }
        return summary;
    }

    // status -> kitne orders
    public Map<String, Integer> getOrderCountsByStatus() throws SQLException {
        Map<String, Integer> counts = new LinkedHashMap<>();
        String sql = "SELECT order_status, COUNT(*) AS total FROM orders GROUP BY order_status ORDER BY total DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                counts.put(rs.getString("order_status"), rs.getInt("total"));
            }
        }
        return counts;
    }

    // Sabse zyada bikne wale products (units ke hisaab se)
    public List<Map<String, Object>> getTopProducts(int limit) throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT p.name, SUM(oi.quantity) AS units, SUM(oi.quantity * oi.price) AS revenue "
                   + "FROM order_items oi "
                   + "JOIN orders o ON oi.order_id = o.order_id "
                   + "JOIN products p ON oi.product_id = p.product_id "
                   + "WHERE o.order_status <> 'CANCELLED' "
                   + "GROUP BY p.product_id, p.name ORDER BY units DESC, revenue DESC LIMIT ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("name", rs.getString("name"));
                    row.put("units", rs.getInt("units"));
                    row.put("revenue", rs.getDouble("revenue"));
                    list.add(row);
                }
            }
        }
        return list;
    }

    // Pichhle 'days' din ki roz ki sales (sirf un dino ki jinmein order hue)
    public List<Map<String, Object>> getDailySales(int days) throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT DATE(created_at) AS sale_date, COUNT(*) AS order_count, "
                   + "COALESCE(SUM(total_amount), 0) AS revenue "
                   + "FROM orders WHERE order_status <> 'CANCELLED' "
                   + "AND created_at >= (CURDATE() - INTERVAL ? DAY) "
                   + "GROUP BY DATE(created_at) ORDER BY sale_date DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, days - 1);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("date", rs.getDate("sale_date"));
                    row.put("orders", rs.getInt("order_count"));
                    row.put("revenue", rs.getDouble("revenue"));
                    list.add(row);
                }
            }
        }
        return list;
    }
}
