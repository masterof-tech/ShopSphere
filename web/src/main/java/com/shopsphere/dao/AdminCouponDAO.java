package com.shopsphere.dao;

import com.shopsphere.model.Coupon;
import com.shopsphere.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Admin ke liye coupons
public class AdminCouponDAO {

    // Saare coupons, kitni baar use hue us ginti ke saath. Har row ek Map hai.
    public List<Map<String, Object>> getAllCoupons() throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT c.*, (SELECT COUNT(*) FROM coupon_usage u WHERE u.coupon_id = c.coupon_id) AS used_count "
                   + "FROM coupons c ORDER BY c.coupon_id DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Date expiry = rs.getDate("expiry_date");
                LocalDate expiryDate = expiry == null ? null : expiry.toLocalDate();

                Map<String, Object> row = new LinkedHashMap<>();
                row.put("couponId", rs.getInt("coupon_id"));
                row.put("code", rs.getString("code"));
                row.put("discountType", rs.getString("discount_type"));
                row.put("discountValue", rs.getDouble("discount_value"));
                row.put("minimumOrder", rs.getDouble("minimum_order"));
                row.put("maximumDiscount", rs.getDouble("maximum_discount"));   // NULL ho to 0
                row.put("expiryDate", expiryDate);
                row.put("expired", expiryDate != null && expiryDate.isBefore(LocalDate.now()));
                row.put("status", rs.getBoolean("status"));
                row.put("usedCount", rs.getInt("used_count"));
                list.add(row);
            }
        }
        return list;
    }

    public Coupon getCouponById(int couponId) throws SQLException {
        String sql = "SELECT * FROM coupons WHERE coupon_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, couponId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Coupon c = new Coupon();
                    c.setCouponId(rs.getInt("coupon_id"));
                    c.setCode(rs.getString("code"));
                    c.setDiscountType(rs.getString("discount_type"));
                    c.setDiscountValue(rs.getDouble("discount_value"));
                    c.setMinimumOrder(rs.getDouble("minimum_order"));
                    c.setMaximumDiscount(rs.getDouble("maximum_discount"));
                    Date expiry = rs.getDate("expiry_date");
                    c.setExpiryDate(expiry == null ? null : expiry.toLocalDate());
                    c.setStatus(rs.getBoolean("status"));
                    return c;
                }
            }
        }
        return null;
    }

    public void insertCoupon(Coupon c) throws SQLException {
        String sql = "INSERT INTO coupons (code, discount_type, discount_value, minimum_order, "
                   + "maximum_discount, expiry_date, status) VALUES (?, ?, ?, ?, ?, ?, TRUE)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            fillCouponFields(ps, c);
            ps.executeUpdate();
        }
    }

    public void updateCoupon(Coupon c) throws SQLException {
        String sql = "UPDATE coupons SET code = ?, discount_type = ?, discount_value = ?, minimum_order = ?, "
                   + "maximum_discount = ?, expiry_date = ? WHERE coupon_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            fillCouponFields(ps, c);
            ps.setInt(7, c.getCouponId());
            ps.executeUpdate();
        }
    }

    public void setCouponStatus(int couponId, boolean status) throws SQLException {
        String sql = "UPDATE coupons SET status = ? WHERE coupon_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBoolean(1, status);
            ps.setInt(2, couponId);
            ps.executeUpdate();
        }
    }

    // Is code ka doosra coupon pehle se hai? (excludeId wale ko ginti mein nahi lete)
    public boolean codeExists(String code, int excludeId) throws SQLException {
        String sql = "SELECT 1 FROM coupons WHERE UPPER(code) = UPPER(?) AND coupon_id <> ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, code);
            ps.setInt(2, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // Insert aur update dono ke pehle 6 parameters same hain
    private void fillCouponFields(PreparedStatement ps, Coupon c) throws SQLException {
        ps.setString(1, c.getCode());
        ps.setString(2, c.getDiscountType());
        ps.setDouble(3, c.getDiscountValue());
        ps.setDouble(4, c.getMinimumOrder());
        if (c.getMaximumDiscount() > 0) {
            ps.setDouble(5, c.getMaximumDiscount());
        } else {
            ps.setNull(5, Types.DECIMAL);      // koi limit nahi
        }
        if (c.getExpiryDate() != null) {
            ps.setDate(6, Date.valueOf(c.getExpiryDate()));
        } else {
            ps.setNull(6, Types.DATE);         // expire nahi hota
        }
    }
}
