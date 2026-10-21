package com.shopsphere.service;

import com.shopsphere.dao.SalesDAO;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class SalesService {

    private final SalesDAO salesDAO = new SalesDAO();

    // Summary ke saath average order value bhi jod do
    public Map<String, Object> getSummary() throws SQLException {
        Map<String, Object> summary = salesDAO.getSummary();
        int orders = (Integer) summary.getOrDefault("validOrders", 0);
        double revenue = (Double) summary.getOrDefault("revenue", 0.0);
        double average = orders == 0 ? 0 : Math.round(revenue / orders * 100.0) / 100.0;
        summary.put("average", average);
        return summary;
    }

    public Map<String, Integer> getOrderCountsByStatus() throws SQLException {
        return salesDAO.getOrderCountsByStatus();
    }

    public List<Map<String, Object>> getTopProducts() throws SQLException {
        return salesDAO.getTopProducts(5);
    }

    public List<Map<String, Object>> getDailySales() throws SQLException {
        return salesDAO.getDailySales(7);
    }
}
