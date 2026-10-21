package com.shopsphere.controller;

import com.shopsphere.service.AdminUserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

// /admin/users -> saare users + customer ko block / unblock
@WebServlet("/admin/users")
public class AdminUserServlet extends HttpServlet {

    private final AdminUserService userService = new AdminUserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setAttribute("users", userService.getUsers());

            HttpSession session = request.getSession();
            Object message = session.getAttribute("adminMessage");
            if (message != null) {
                request.setAttribute("adminMessage", message);
                request.setAttribute("adminMessageType", session.getAttribute("adminMessageType"));
                session.removeAttribute("adminMessage");
                session.removeAttribute("adminMessageType");
            }

            request.getRequestDispatcher("/admin/users.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Something went wrong");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();

        if ("toggle".equals(request.getParameter("action"))) {
            try {
                boolean unblock = "unblock".equals(request.getParameter("status"));
                String error = userService.setCustomerStatus(parseInt(request.getParameter("userId"), 0), unblock);

                session.setAttribute("adminMessage", error == null
                        ? (unblock ? "Customer unblocked" : "Customer blocked") : error);
                session.setAttribute("adminMessageType", error == null ? "info" : "danger");
            } catch (SQLException e) {
                e.printStackTrace();
                session.setAttribute("adminMessage", "Something went wrong. Please try again.");
                session.setAttribute("adminMessageType", "danger");
            }
        }

        response.sendRedirect(request.getContextPath() + "/admin/users");
    }

    private int parseInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException | NullPointerException e) {
            return defaultValue;
        }
    }
}
