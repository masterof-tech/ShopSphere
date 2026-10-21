package com.shopsphere.controller;

import com.shopsphere.service.AdminService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

// Admin dashboard. Sirf admin aa sakta hai (AdminFilter /admin/* ko rokta hai).
@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {

    private final AdminService adminService = new AdminService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setAttribute("customerCount", adminService.getCustomerCount());
            request.setAttribute("productCount", adminService.getProductCount());
            request.setAttribute("orderCount", adminService.getOrderCount());
            request.setAttribute("revenue", adminService.getRevenue());
            request.setAttribute("lowStockLimit", AdminService.LOW_STOCK_LIMIT);
            request.setAttribute("lowStockProducts", adminService.getLowStockProducts());
            request.setAttribute("recentOrders", adminService.getRecentOrders());

            request.getRequestDispatcher("/admin/dashboard.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Something went wrong");
        }
    }
}
