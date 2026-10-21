package com.shopsphere.service;

import com.shopsphere.dao.AdminCouponDAO;
import com.shopsphere.model.Coupon;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

public class AdminCouponService {

    private final AdminCouponDAO couponDAO = new AdminCouponDAO();

    public List<Map<String, Object>> getAllCoupons() throws SQLException {
        return couponDAO.getAllCoupons();
    }

    public Coupon getCoupon(int id) throws SQLException {
        return couponDAO.getCouponById(id);
    }

    public void setCouponStatus(int couponId, boolean status) throws SQLException {
        couponDAO.setCouponStatus(couponId, status);
    }

    // Sab theek ho to null, warna error message.
    // couponId > 0 matlab edit, 0 matlab naya coupon. expiryText "yyyy-MM-dd" ya khali.
    public String saveCoupon(Coupon c, String expiryText) throws SQLException {
        String code = c.getCode() == null ? "" : c.getCode().trim().toUpperCase();
        c.setCode(code);

        String expiry = expiryText == null ? "" : expiryText.trim();
        if (expiry.isEmpty()) {
            c.setExpiryDate(null);
        } else {
            try {
                c.setExpiryDate(LocalDate.parse(expiry));
            } catch (DateTimeParseException e) {
                c.setExpiryDate(null);
                return "Please enter a valid expiry date";
            }
        }

        String type = c.getDiscountType();
        if (code.length() < 3 || code.length() > 30 || !code.matches("[A-Z0-9_-]+")) {
            return "Coupon code must be 3 to 30 characters (letters, numbers, dash or underscore)";
        }
        if (!"PERCENT".equals(type) && !"FLAT".equals(type)) {
            return "Please select a discount type";
        }
        if (c.getDiscountValue() <= 0) {
            return "Discount value must be greater than 0";
        }
        if ("PERCENT".equals(type) && c.getDiscountValue() > 100) {
            return "A percentage discount cannot be more than 100";
        }
        if (c.getMinimumOrder() < 0) {
            return "Minimum order must be 0 or more";
        }
        if (c.getMaximumDiscount() < 0) {
            return "Maximum discount must be 0 or more";
        }
        if ("FLAT".equals(type)) {
            c.setMaximumDiscount(0);     // limit sirf percentage coupon ke liye hoti hai
        }
        if (couponDAO.codeExists(code, c.getCouponId())) {
            return "A coupon with this code already exists";
        }

        if (c.getCouponId() > 0) {
            if (couponDAO.getCouponById(c.getCouponId()) == null) {
                return "Coupon not found";
            }
            couponDAO.updateCoupon(c);
        } else {
            couponDAO.insertCoupon(c);
        }
        return null;
    }
}
