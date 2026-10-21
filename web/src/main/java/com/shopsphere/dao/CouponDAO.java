package com.shopsphere.dao;

import com.shopsphere.model.Coupon;
import com.shopsphere.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CouponDAO {

    // Code se coupon dhoondo (capital/small letters ka farak nahi)
    public Coupon findByCode(String code) throws SQLException {
        String sql = "SELECT * FROM coupons WHERE UPPER(code) = UPPER(?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Coupon c = new Coupon();
                    c.setCouponId(rs.getInt("coupon_id"));
                    c.setCode(rs.getString("code"));
                    c.setDiscountType(rs.getString("discount_type"));
                    c.setDiscountValue(rs.getDouble("discount_value"));
                    c.setMinimumOrder(rs.getDouble("minimum_order"));
                    c.setMaximumDiscount(rs.getDouble("maximum_discount"));   // NULL ho to 0
                    Date expiry = rs.getDate("expiry_date");
                    c.setExpiryDate(expiry == null ? null : expiry.toLocalDate());
                    c.setStatus(rs.getBoolean("status"));
                    return c;
                }
            }
        }
        return null;
    }

    // Is user ne ye coupon pehle use kiya hai ya nahi
    public boolean hasUserUsedCoupon(int couponId, int userId) throws SQLException {
        String sql = "SELECT 1 FROM coupon_usage WHERE coupon_id = ? AND user_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, couponId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}
