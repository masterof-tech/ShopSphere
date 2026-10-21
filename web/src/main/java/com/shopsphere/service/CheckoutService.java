package com.shopsphere.service;

import com.shopsphere.dao.AddressDAO;
import com.shopsphere.dao.CartDAO;
import com.shopsphere.dao.CouponDAO;
import com.shopsphere.dao.OrderDAO;
import com.shopsphere.model.Address;
import com.shopsphere.model.CartItem;
import com.shopsphere.model.Coupon;
import com.shopsphere.model.Order;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class CheckoutService {

    private static final List<String> PAYMENT_METHODS = Arrays.asList("COD", "UPI", "CARD");

    private final AddressDAO addressDAO = new AddressDAO();
    private final CouponDAO couponDAO = new CouponDAO();
    private final CartDAO cartDAO = new CartDAO();
    private final OrderDAO orderDAO = new OrderDAO();

    // Coupon check ka result: ya to discount milega, ya error message
    public static class CouponCheck {
        private final Coupon coupon;
        private final double discount;
        private final String error;

        public CouponCheck(Coupon coupon, double discount, String error) {
            this.coupon = coupon;
            this.discount = discount;
            this.error = error;
        }
        public Coupon getCoupon() { return coupon; }
        public double getDiscount() { return discount; }
        public String getError() { return error; }
    }

    public List<Address> getAddresses(int userId) throws SQLException {
        return addressDAO.getAddressesByUser(userId);
    }

    // Sab theek ho to null, warna error message
    public String addAddress(int userId, String line, String city, String state,
                             String pincode, String type) throws SQLException {
        line = line == null ? "" : line.trim();
        city = city == null ? "" : city.trim();
        state = state == null ? "" : state.trim();
        pincode = pincode == null ? "" : pincode.trim();

        if (line.isEmpty()) {
            return "Address line is required";
        }
        if (city.isEmpty()) {
            return "City is required";
        }
        if (state.isEmpty()) {
            return "State is required";
        }
        if (!pincode.matches("\\d{6}")) {
            return "PIN code must be 6 digits";
        }
        if (!"HOME".equals(type) && !"WORK".equals(type) && !"OTHER".equals(type)) {
            type = "HOME";
        }

        Address a = new Address();
        a.setUserId(userId);
        a.setAddressLine(line);
        a.setCity(city);
        a.setState(state);
        a.setPincode(pincode);
        a.setAddressType(type);
        addressDAO.addAddress(a);
        return null;
    }

    public CouponCheck applyCoupon(String code, double subtotal, int userId) throws SQLException {
        if (code == null || code.trim().isEmpty()) {
            return new CouponCheck(null, 0, null);   // coupon daala hi nahi
        }

        Coupon coupon = couponDAO.findByCode(code.trim());
        if (coupon == null) {
            return failed("Invalid coupon code");
        }
        if (!coupon.isStatus()) {
            return failed("This coupon is not active");
        }
        if (coupon.getExpiryDate() != null && coupon.getExpiryDate().isBefore(LocalDate.now())) {
            return failed("This coupon has expired");
        }
        if (subtotal < coupon.getMinimumOrder()) {
            return failed("Minimum order for this coupon is Rs. " + coupon.getMinimumOrder());
        }
        if (couponDAO.hasUserUsedCoupon(coupon.getCouponId(), userId)) {
            return failed("You have already used this coupon");
        }

        double discount;
        if ("PERCENT".equals(coupon.getDiscountType())) {
            discount = subtotal * coupon.getDiscountValue() / 100;
            if (coupon.getMaximumDiscount() > 0 && discount > coupon.getMaximumDiscount()) {
                discount = coupon.getMaximumDiscount();
            }
        } else {
            discount = coupon.getDiscountValue();
        }
        if (discount > subtotal) {
            discount = subtotal;
        }
        return new CouponCheck(coupon, round2(discount), null);
    }

    // Order place karo. Galti ho to IllegalStateException (message user ko dikhega).
    // Saari values yahan database se dobara nikalte hain, browser ki values par bharosa nahi.
    public int placeOrder(int userId, int addressId, String paymentMethod, String couponCode)
            throws SQLException {

        List<CartItem> items = cartDAO.getCartItems(userId);
        if (items.isEmpty()) {
            throw new IllegalStateException("Your cart is empty");
        }

        Address address = addressDAO.getAddress(addressId, userId);
        if (address == null) {
            throw new IllegalStateException("Please select a delivery address");
        }

        if (paymentMethod == null || !PAYMENT_METHODS.contains(paymentMethod)) {
            throw new IllegalStateException("Please select a payment method");
        }

        double subtotal = 0;
        for (CartItem item : items) {
            subtotal += item.getSubtotal();
        }
        subtotal = round2(subtotal);

        CouponCheck check = applyCoupon(couponCode, subtotal, userId);
        if (check.getError() != null) {
            throw new IllegalStateException(check.getError());
        }

        Order order = new Order();
        order.setUserId(userId);
        order.setAddressId(addressId);
        order.setDiscount(check.getDiscount());
        order.setTotalAmount(round2(subtotal - check.getDiscount()));
        order.setPaymentMethod(paymentMethod);
        order.setOrderStatus("PLACED");

        // Demo payment: COD ka payment baad mein hota hai, baaki ko success maan lete hain
        String transactionRef = null;
        if ("COD".equals(paymentMethod)) {
            order.setPaymentStatus("PENDING");
        } else {
            order.setPaymentStatus("SUCCESS");
            transactionRef = "TXN" + System.currentTimeMillis();
        }

        Integer couponId = check.getCoupon() == null ? null : check.getCoupon().getCouponId();
        return orderDAO.placeOrder(order, items, couponId, transactionRef);
    }

    public Order getOrder(int orderId, int userId) throws SQLException {
        return orderDAO.getOrderById(orderId, userId);
    }

    private CouponCheck failed(String message) {
        return new CouponCheck(null, 0, message);
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
