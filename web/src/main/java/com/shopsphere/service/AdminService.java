package com.shopsphere.service;

import com.shopsphere.dao.AdminDAO;
import com.shopsphere.model.Product;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class AdminService {

    public static final int LOW_STOCK_LIMIT = 5;

    private final AdminDAO adminDAO = new AdminDAO();

    public int getCustomerCount() throws SQLException {
        return adminDAO.countCustomers();
    }

    public int getProductCount() throws SQLException {
        return adminDAO.countProducts();
    }

    public int getOrderCount() throws SQLException {
        return adminDAO.countOrders();
    }

    public double getRevenue() throws SQLException {
        return Math.round(adminDAO.totalRevenue() * 100.0) / 100.0;
    }

    public List<Product> getLowStockProducts() throws SQLException {
        return adminDAO.getLowStockProducts(LOW_STOCK_LIMIT);
    }

    public List<Map<String, Object>> getRecentOrders() throws SQLException {
        return adminDAO.getRecentOrders(5);
    }
}
