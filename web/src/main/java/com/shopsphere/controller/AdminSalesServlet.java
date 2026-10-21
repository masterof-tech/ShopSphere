package com.shopsphere.controller;

import com.shopsphere.service.SalesService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

// Admin ka sales summary page (AdminFilter /admin/* ko rokta hai)
@WebServlet("/admin/sales")
public class AdminSalesServlet extends HttpServlet {

    private final SalesService salesService = new SalesService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setAttribute("summary", salesService.getSummary());
            request.setAttribute("statusCounts", salesService.getOrderCountsByStatus());
            request.setAttribute("topProducts", salesService.getTopProducts());
            request.setAttribute("dailySales", salesService.getDailySales());

            request.getRequestDispatcher("/admin/sales.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Something went wrong");
        }
    }
}
