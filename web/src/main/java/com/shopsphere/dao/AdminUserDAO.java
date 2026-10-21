package com.shopsphere.dao;

import com.shopsphere.model.User;
import com.shopsphere.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Admin ke liye users
public class AdminUserDAO {

    // Saare users, har ek ke orders ki ginti ke saath. Har row ek Map hai.
    public List<Map<String, Object>> getAllUsers() throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT u.user_id, u.name, u.email, u.mobile, u.role, u.status, u.created_at, "
                   + "(SELECT COUNT(*) FROM orders o WHERE o.user_id = u.user_id) AS order_count "
                   + "FROM users u ORDER BY u.user_id";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("userId", rs.getInt("user_id"));
                row.put("name", rs.getString("name"));
                row.put("email", rs.getString("email"));
                row.put("mobile", rs.getString("mobile"));
                row.put("role", rs.getString("role"));
                row.put("status", rs.getBoolean("status"));
                row.put("createdAt", rs.getTimestamp("created_at"));
                row.put("orderCount", rs.getInt("order_count"));
                list.add(row);
            }
        }
        return list;
    }

    public User getUserById(int userId) throws SQLException {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User u = new User();
                    u.setUserId(rs.getInt("user_id"));
                    u.setName(rs.getString("name"));
                    u.setEmail(rs.getString("email"));
                    u.setMobile(rs.getString("mobile"));
                    u.setRole(rs.getString("role"));
                    u.setStatus(rs.getBoolean("status"));
                    u.setCreatedAt(rs.getTimestamp("created_at"));
                    return u;      // password jaan-boojhkar nahi bheja
                }
            }
        }
        return null;
    }

    public void setUserStatus(int userId, boolean status) throws SQLException {
        String sql = "UPDATE users SET status = ? WHERE user_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBoolean(1, status);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }
}
