package com.shopsphere.dao;

import com.shopsphere.model.CartItem;
import com.shopsphere.model.Order;
import com.shopsphere.model.OrderItem;
import com.shopsphere.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    // Order place karna ek TRANSACTION hai: ya to saari steps hongi, ya koi bhi nahi.
    // Steps: order banao -> items save + stock kam karo -> payment save -> coupon use mark karo -> cart khali karo
    public int placeOrder(Order order, List<CartItem> items, Integer couponId, String transactionRef)
            throws SQLException {

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                int orderId;

                // 1. orders table
                String orderSql = "INSERT INTO orders (user_id, address_id, total_amount, discount, "
                                + "payment_method, payment_status, order_status) VALUES (?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = con.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, order.getUserId());
                    ps.setInt(2, order.getAddressId());
                    ps.setDouble(3, order.getTotalAmount());
                    ps.setDouble(4, order.getDiscount());
                    ps.setString(5, order.getPaymentMethod());
                    ps.setString(6, order.getPaymentStatus());
                    ps.setString(7, order.getOrderStatus());
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        keys.next();
                        orderId = keys.getInt(1);
                    }
                }

                // 2. order_items + stock kam karna
                String stockSql = "UPDATE products SET stock = stock - ? WHERE product_id = ? AND stock >= ?";
                String itemSql = "INSERT INTO order_items (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
                for (CartItem item : items) {
                    try (PreparedStatement ps = con.prepareStatement(stockSql)) {
                        ps.setInt(1, item.getQuantity());
                        ps.setInt(2, item.getProductId());
                        ps.setInt(3, item.getQuantity());
                        if (ps.executeUpdate() == 0) {
                            throw new IllegalStateException("Not enough stock for " + item.getProductName());
                        }
                    }
                    try (PreparedStatement ps = con.prepareStatement(itemSql)) {
                        ps.setInt(1, orderId);
                        ps.setInt(2, item.getProductId());
                        ps.setInt(3, item.getQuantity());
                        ps.setDouble(4, item.getUnitPrice());
                        ps.executeUpdate();
                    }
                }

                // 3. payments table
                String paySql = "INSERT INTO payments (order_id, payment_method, transaction_reference, amount, payment_status) "
                              + "VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement ps = con.prepareStatement(paySql)) {
                    ps.setInt(1, orderId);
                    ps.setString(2, order.getPaymentMethod());
                    ps.setString(3, transactionRef);
                    ps.setDouble(4, order.getTotalAmount());
                    ps.setString(5, order.getPaymentStatus());
                    ps.executeUpdate();
                }

                // 4. coupon use hua
                if (couponId != null) {
                    String useSql = "INSERT INTO coupon_usage (coupon_id, user_id, order_id) VALUES (?, ?, ?)";
                    try (PreparedStatement ps = con.prepareStatement(useSql)) {
                        ps.setInt(1, couponId);
                        ps.setInt(2, order.getUserId());
                        ps.setInt(3, orderId);
                        ps.executeUpdate();
                    }
                }

                // 5. cart khali
                try (PreparedStatement ps = con.prepareStatement("DELETE FROM cart WHERE user_id = ?")) {
                    ps.setInt(1, order.getUserId());
                    ps.executeUpdate();
                }

                con.commit();
                return orderId;

            } catch (SQLException | RuntimeException e) {
                con.rollback();      // kuch bhi fail ho to sab undo
                throw e;
            }
        }
    }

    // Order sirf usi user ko milega jiska wo hai
    public Order getOrderById(int orderId, int userId) throws SQLException {
        String sql = "SELECT * FROM orders WHERE order_id = ? AND user_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapOrder(rs);
                }
            }
        }
        return null;
    }

    // User ke saare orders, naya order pehle
    public List<Order> getOrdersByUser(int userId) throws SQLException {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE user_id = ? ORDER BY order_id DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapOrder(rs));
                }
            }
        }
        return list;
    }

    // Ek order ke items (product ke naam ke saath)
    public List<OrderItem> getOrderItems(int orderId) throws SQLException {
        List<OrderItem> list = new ArrayList<>();
        String sql = "SELECT oi.order_item_id, oi.order_id, oi.product_id, oi.quantity, oi.price, "
                   + "p.name, p.image_url "
                   + "FROM order_items oi JOIN products p ON oi.product_id = p.product_id "
                   + "WHERE oi.order_id = ? ORDER BY oi.order_item_id";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderItem item = new OrderItem();
                    item.setOrderItemId(rs.getInt("order_item_id"));
                    item.setOrderId(rs.getInt("order_id"));
                    item.setProductId(rs.getInt("product_id"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setPrice(rs.getDouble("price"));
                    item.setProductName(rs.getString("name"));
                    item.setImageUrl(rs.getString("image_url"));
                    list.add(item);
                }
            }
        }
        return list;
    }

    // Order cancel karna (sirf PLACED order). Ye bhi ek TRANSACTION hai:
    // stock wapas -> order aur payment ka status badlo -> coupon wapas
    // Galti ho to IllegalStateException (message user ko dikhega)
    public void cancelOrder(int orderId, int userId) throws SQLException {
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                // 1. order sahi user ka hai aur abhi cancel ho sakta hai?
                String status = null;
                String lockSql = "SELECT order_status FROM orders WHERE order_id = ? AND user_id = ? FOR UPDATE";
                try (PreparedStatement ps = con.prepareStatement(lockSql)) {
                    ps.setInt(1, orderId);
                    ps.setInt(2, userId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new IllegalStateException("Order not found");
                        }
                        status = rs.getString("order_status");
                    }
                }
                if (!"PLACED".equals(status)) {
                    throw new IllegalStateException("This order can no longer be cancelled");
                }

                // 2. stock wapas
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

                // 3. order + payment status (paid tha to REFUNDED, warna CANCELLED)
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

                // 4. coupon wapas (user dobara use kar sakta hai)
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

    private Order mapOrder(ResultSet rs) throws SQLException {
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
