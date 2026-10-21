package com.shopsphere.service;

import com.shopsphere.dao.AddressDAO;
import com.shopsphere.dao.OrderDAO;
import com.shopsphere.model.Address;
import com.shopsphere.model.Order;
import com.shopsphere.model.OrderItem;

import java.sql.SQLException;
import java.util.List;

public class OrderService {

    private final OrderDAO orderDAO = new OrderDAO();
    private final AddressDAO addressDAO = new AddressDAO();

    public List<Order> getOrders(int userId) throws SQLException {
        return orderDAO.getOrdersByUser(userId);
    }

    // Order tabhi milega jab wo isi user ka ho, warna null
    public Order getOrder(int orderId, int userId) throws SQLException {
        return orderDAO.getOrderById(orderId, userId);
    }

    public List<OrderItem> getItems(int orderId) throws SQLException {
        return orderDAO.getOrderItems(orderId);
    }

    public Address getAddress(Order order) throws SQLException {
        return addressDAO.getAddress(order.getAddressId(), order.getUserId());
    }

    // Cancel ho gaya to null, warna user ko dikhane wala message
    public String cancelOrder(int orderId, int userId) throws SQLException {
        try {
            orderDAO.cancelOrder(orderId, userId);
            return null;
        } catch (IllegalStateException e) {
            return e.getMessage();
        }
    }
}
