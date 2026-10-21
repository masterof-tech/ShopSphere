package com.shopsphere.controller;

import com.shopsphere.model.User;
import com.shopsphere.service.ProfileService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

// /profile -> profile page (GET); naam/mobile, password, address add/delete (POST)
@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

    private final ProfileService profileService = new ProfileService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User sessionUser = getUser(request);
        if (sessionUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            // Taza data database se
            User profile = profileService.getUser(sessionUser.getUserId());
            if (profile == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
            request.setAttribute("profile", profile);
            request.setAttribute("addresses", profileService.getAddresses(sessionUser.getUserId()));

            HttpSession session = request.getSession();
            Object message = session.getAttribute("profileMessage");
            if (message != null) {
                request.setAttribute("profileMessage", message);
                request.setAttribute("profileMessageType", session.getAttribute("profileMessageType"));
                session.removeAttribute("profileMessage");
                session.removeAttribute("profileMessageType");
            }

            request.getRequestDispatcher("/profile.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Something went wrong");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        User user = getUser(request);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");
        HttpSession session = request.getSession();
        String error = null;
        String success = null;

        try {
            if ("updateProfile".equals(action)) {
                error = profileService.updateProfile(user.getUserId(),
                        request.getParameter("name"), request.getParameter("mobile"));
                success = "Your details have been updated";
                if (error == null) {
                    // Navbar mein naya naam dikhe, isliye session ka user bhi taza karo
                    session.setAttribute("user", profileService.getUser(user.getUserId()));
                }

            } else if ("changePassword".equals(action)) {
                error = profileService.changePassword(user,
                        request.getParameter("currentPassword"),
                        request.getParameter("newPassword"),
                        request.getParameter("confirmPassword"));
                success = "Your password has been changed";

            } else if ("addAddress".equals(action)) {
                error = profileService.addAddress(user.getUserId(),
                        request.getParameter("addressLine"),
                        request.getParameter("city"),
                        request.getParameter("state"),
                        request.getParameter("pincode"),
                        request.getParameter("addressType"));
                success = "Address added successfully";

            } else if ("deleteAddress".equals(action)) {
                error = profileService.deleteAddress(user.getUserId(),
                        parseInt(request.getParameter("addressId"), 0));
                success = "Address deleted";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            error = "Something went wrong. Please try again.";
        }

        if (success != null || error != null) {
            session.setAttribute("profileMessage", error == null ? success : error);
            session.setAttribute("profileMessageType", error == null ? "success" : "danger");
        }
        response.sendRedirect(request.getContextPath() + "/profile");
    }

    private User getUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session == null ? null : (User) session.getAttribute("user");
    }

    private int parseInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException | NullPointerException e) {
            return defaultValue;
        }
    }
}
