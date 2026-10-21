package com.shopsphere.model;

import java.time.LocalDate;

public class Coupon {
    private int couponId;
    private String code;
    private String discountType;     // PERCENT ya FLAT
    private double discountValue;
    private double minimumOrder;
    private double maximumDiscount;  // 0 matlab koi limit nahi
    private LocalDate expiryDate;    // null matlab expire nahi hota
    private boolean status;

    public Coupon() {}

    public int getCouponId() { return couponId; }
    public void setCouponId(int couponId) { this.couponId = couponId; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getDiscountType() { return discountType; }
    public void setDiscountType(String discountType) { this.discountType = discountType; }

    public double getDiscountValue() { return discountValue; }
    public void setDiscountValue(double discountValue) { this.discountValue = discountValue; }

    public double getMinimumOrder() { return minimumOrder; }
    public void setMinimumOrder(double minimumOrder) { this.minimumOrder = minimumOrder; }

    public double getMaximumDiscount() { return maximumDiscount; }
    public void setMaximumDiscount(double maximumDiscount) { this.maximumDiscount = maximumDiscount; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public boolean isStatus() { return status; }
    public void setStatus(boolean status) { this.status = status; }
}
