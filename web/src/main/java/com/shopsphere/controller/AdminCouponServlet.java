package com.shopsphere.controller;

import com.shopsphere.model.Coupon;
import com.shopsphere.service.AdminCouponService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

// /admin/coupons          -> list + naya coupon form
// /admin/coupons?edit=3   -> wahi page, form mein coupon 3 bhara hua
@WebServlet("/admin/coupons")
public class AdminCouponServlet extends HttpServlet {

    private final AdminCouponService couponService = new AdminCouponService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Coupon editCoupon = null;
            int editId = parseInt(request.getParameter("edit"), 0);
            if (editId > 0) {
                editCoupon = couponService.getCoupon(editId);
            }
            showPage(request, response, editCoupon);
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Something went wrong");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        HttpSession session = request.getSession();

        try {
            if ("save".equals(action)) {
                Coupon c = new Coupon();
                c.setCouponId(parseInt(request.getParameter("couponId"), 0));
                c.setCode(request.getParameter("code"));
                c.setDiscountType(request.getParameter("discountType"));
                c.setDiscountValue(parseDouble(request.getParameter("discountValue"), 0));
                c.setMinimumOrder(blank(request.getParameter("minimumOrder")) ? 0 : parseDouble(request.getParameter("minimumOrder"), -1));
                c.setMaximumDiscount(blank(request.getParameter("maximumDiscount")) ? 0 : parseDouble(request.getParameter("maximumDiscount"), -1));

                String error = couponService.saveCoupon(c, request.getParameter("expiryDate"));
                if (error != null) {
                    request.setAttribute("error", error);
                    showPage(request, response, c);      // bhari hui values ke saath form wapas
                    return;
                }
                session.setAttribute("adminMessage", "Coupon saved successfully");
                session.setAttribute("adminMessageType", "success");

            } else if ("toggle".equals(action)) {
                boolean enable = "enable".equals(request.getParameter("status"));
                couponService.setCouponStatus(parseInt(request.getParameter("couponId"), 0), enable);
                session.setAttribute("adminMessage", enable ? "Coupon enabled" : "Coupon disabled");
                session.setAttribute("adminMessageType", "info");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            session.setAttribute("adminMessage", "Something went wrong. Please try again.");
            session.setAttribute("adminMessageType", "danger");
        }

        response.sendRedirect(request.getContextPath() + "/admin/coupons");
    }

    private void showPage(HttpServletRequest request, HttpServletResponse response, Coupon editCoupon)
            throws ServletException, IOException, SQLException {
        request.setAttribute("coupons", couponService.getAllCoupons());
        request.setAttribute("editCoupon", editCoupon);

        HttpSession session = request.getSession();
        Object message = session.getAttribute("adminMessage");
        if (message != null) {
            request.setAttribute("adminMessage", message);
            request.setAttribute("adminMessageType", session.getAttribute("adminMessageType"));
            session.removeAttribute("adminMessage");
            session.removeAttribute("adminMessageType");
        }

        request.getRequestDispatcher("/admin/coupons.jsp").forward(request, response);
    }

    private boolean blank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private int parseInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException | NullPointerException e) {
            return defaultValue;
        }
    }

    private double parseDouble(String value, double defaultValue) {
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException | NullPointerException e) {
            return defaultValue;
        }
    }
}
