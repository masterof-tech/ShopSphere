package com.shopsphere.dao;

import com.shopsphere.model.Order;
import com.shopsphere.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Admin ke liye orders: saare users ke orders dekhna, status badalna, cancel karna
public class AdminOrderDAO {

    // Saare orders (status null ho to sab), customer ke naam ke saath.
    // Har row ek Map hai: JSP mein ${row.orderId} jaise padhte hain
    public List<Map<String, Object>> getOrders(String status) throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT o.order_id, o.total_amount, o.payment_method, o.payment_status, "
                   + "o.order_status, o.created_at, u.name "
                   + "FROM orders o JOIN users u ON o.user_id = u.user_id "
                   + (status == null ? "" : "WHERE o.order_status = ? ")
                   + "ORDER BY o.order_id DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (status != null) {
                ps.setString(1, status);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("orderId", rs.getInt("order_id"));
                    row.put("customer", rs.getString("name"));
                    row.put("total", rs.getDouble("total_amount"));
                    row.put("paymentMethod", rs.getString("payment_method"));
                    row.put("paymentStatus", rs.getString("payment_status"));
                    row.put("status", rs.getString("order_status"));
                    row.put("createdAt", rs.getTimestamp("created_at"));
                    list.add(row);
                }
            }
        }
        return list;
    }

    // Kisi bhi user ka order (admin ke liye user ki shart nahi)
    public Order getOrder(int orderId) throws SQLException {
        String sql = "SELECT * FROM orders WHERE order_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Order o = new Order();
                    o.setOrderId(rs.getInt("order_id"));
                    o.setUserId(rs.getInt("user_id"));
                    o.setAddressId(rs.getInt("address_id"));
                    o.setTotalAmount(rs.getDouble("total_amount"));
                    o.setDiscount(rs.getDouble("discount"));
                    o.setPaymentMethod(rs.getString("payment_method"));
                    o.setPaymentStatus(rs.getString("payment_status"));
                    o.setOrderStatus(rs.getString("order_status"));
                    o.setCreatedAt(rs.getTimestamp("created_at"));
                    return o;
                }
            }
        }
        return null;
    }

    // Status aage badhao (fromStatus -> toStatus). Transaction: ya sab hoga ya kuch nahi.
    // Delivered hone par COD ka paisa mil gaya maan lete hain (payment PENDING -> SUCCESS).
    // Galti ho to IllegalStateException (message admin ko dikhega)
    public void updateStatus(int orderId, String fromStatus, String toStatus) throws SQLException {
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                int rows;
                // "AND order_status = from": agar kisi ne beech mein status badal diya to update nahi hoga
                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE orders SET order_status = ? WHERE order_id = ? AND order_status = ?")) {
                    ps.setString(1, toStatus);
                    ps.setInt(2, orderId);
                    ps.setString(3, fromStatus);
                    rows = ps.executeUpdate();
                }
                if (rows == 0) {
                    throw new IllegalStateException("The order status has changed. Please refresh the page.");
                }

                if ("DELIVERED".equals(toStatus)) {
                    try (PreparedStatement ps = con.prepareStatement(
                            "UPDATE orders SET payment_status = 'SUCCESS' WHERE order_id = ? AND payment_status = 'PENDING'")) {
                        ps.setInt(1, orderId);
                        ps.executeUpdate();
                    }
                    try (PreparedStatement ps = con.prepareStatement(
                            "UPDATE payments SET payment_status = 'SUCCESS' WHERE order_id = ? AND payment_status = 'PENDING'")) {
                        ps.setInt(1, orderId);
                        ps.executeUpdate();
                    }
                }

                con.commit();

            } catch (SQLException | RuntimeException e) {
                con.rollback();
                throw e;
            }
        }
    }

    // Admin order cancel kar sakta hai jab status PLACED ya CONFIRMED ho.
    // Transaction: stock wapas -> order aur payment ka status badlo -> coupon wapas
    public void cancelOrder(int orderId) throws SQLException {
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                String status = null;
                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT order_status FROM orders WHERE order_id = ? FOR UPDATE")) {
                    ps.setInt(1, orderId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new IllegalStateException("Order not found");
                        }
                        status = rs.getString("order_status");
                    }
                }
                if (!"PLACED".equals(status) && !"CONFIRMED".equals(status)) {
                    throw new IllegalStateException("This order can no longer be cancelled");
                }

                // stock wapas
                List<int[]> lines = new ArrayList<>();
                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT product_id, quantity FROM order_items WHERE order_id = ?")) {
                    ps.setInt(1, orderId);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            lines.add(new int[] { rs.getInt("product_id"), rs.getInt("quantity") });
                        }
                    }
                }
                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE products SET stock = stock + ? WHERE product_id = ?")) {
                    for (int[] line : lines) {
                        ps.setInt(1, line[1]);
                        ps.setInt(2, line[0]);
                        ps.executeUpdate();
                    }
                }

                // paid tha to REFUNDED, warna CANCELLED
                String newPaymentStatus = "CASE WHEN payment_status = 'SUCCESS' THEN 'REFUNDED' ELSE 'CANCELLED' END";
                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE orders SET order_status = 'CANCELLED', payment_status = "
                        + newPaymentStatus + " WHERE order_id = ?")) {
                    ps.setInt(1, orderId);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE payments SET payment_status = " + newPaymentStatus + " WHERE order_id = ?")) {
                    ps.setInt(1, orderId);
                    ps.executeUpdate();
                }

                // coupon wapas
                try (PreparedStatement ps = con.prepareStatement("DELETE FROM coupon_usage WHERE order_id = ?")) {
                    ps.setInt(1, orderId);
                    ps.executeUpdate();
                }

                con.commit();

            } catch (SQLException | RuntimeException e) {
                con.rollback();
                throw e;
            }
        }
    }
}
