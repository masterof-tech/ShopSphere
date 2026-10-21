package com.shopsphere.service;

import com.shopsphere.dao.AddressDAO;
import com.shopsphere.dao.AdminOrderDAO;
import com.shopsphere.dao.AdminUserDAO;
import com.shopsphere.dao.OrderDAO;
import com.shopsphere.model.Address;
import com.shopsphere.model.Order;
import com.shopsphere.model.OrderItem;
import com.shopsphere.model.User;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class AdminOrderService {

    public static final List<String> STATUSES =
            Arrays.asList("PLACED", "CONFIRMED", "SHIPPED", "DELIVERED", "CANCELLED");

    // Status sirf is order mein aage badh sakta hai (peeche ya beech mein kood nahi sakta)
    private static final Map<String, String> NEXT_STATUS = Map.of(
            "PLACED", "CONFIRMED",
            "CONFIRMED", "SHIPPED",
            "SHIPPED", "DELIVERED");

    private final AdminOrderDAO adminOrderDAO = new AdminOrderDAO();
    private final AdminUserDAO adminUserDAO = new AdminUserDAO();
    private final OrderDAO orderDAO = new OrderDAO();
    private final AddressDAO addressDAO = new AddressDAO();

    // status galat ya khali ho to saare orders
    public List<Map<String, Object>> getOrders(String status) throws SQLException {
        return adminOrderDAO.getOrders(STATUSES.contains(status) ? status : null);
    }

    public Order getOrder(int orderId) throws SQLException {
        return adminOrderDAO.getOrder(orderId);
    }

    public List<OrderItem> getItems(int orderId) throws SQLException {
        return orderDAO.getOrderItems(orderId);
    }

    public Address getAddress(Order order) throws SQLException {
        return addressDAO.getAddress(order.getAddressId(), order.getUserId());
    }

    public User getCustomer(Order order) throws SQLException {
        return adminUserDAO.getUserById(order.getUserId());
    }

    // Agla status (nahi hai to null)
    public String getNextStatus(String current) {
        return NEXT_STATUS.get(current);
    }

    public boolean canCancel(String current) {
        return "PLACED".equals(current) || "CONFIRMED".equals(current);
    }

    // Status ek kadam aage badhao. Sab theek ho to null, warna error message.
    public String advanceStatus(int orderId) throws SQLException {
        Order order = adminOrderDAO.getOrder(orderId);
        if (order == null) {
            return "Order not found";
        }
        String next = getNextStatus(order.getOrderStatus());
        if (next == null) {
            return "This order cannot be moved to a new status";
        }
        try {
            adminOrderDAO.updateStatus(orderId, order.getOrderStatus(), next);
            return null;
        } catch (IllegalStateException e) {
            return e.getMessage();
        }
    }

    public String cancelOrder(int orderId) throws SQLException {
        try {
            adminOrderDAO.cancelOrder(orderId);
            return null;
        } catch (IllegalStateException e) {
            return e.getMessage();
        }
    }
}
