package com.shopsphere.service;

import com.shopsphere.dao.AdminUserDAO;
import com.shopsphere.model.User;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class AdminUserService {

    private final AdminUserDAO adminUserDAO = new AdminUserDAO();

    public List<Map<String, Object>> getUsers() throws SQLException {
        return adminUserDAO.getAllUsers();
    }

    // Customer ko block (status false) ya unblock (status true) karo.
    // Sab theek ho to null, warna error message. Admin account ko chhedne nahi dete.
    public String setCustomerStatus(int userId, boolean status) throws SQLException {
        User target = adminUserDAO.getUserById(userId);
        if (target == null) {
            return "User not found";
        }
        if (!"CUSTOMER".equals(target.getRole())) {
            return "Only customer accounts can be blocked or unblocked";
        }
        adminUserDAO.setUserStatus(userId, status);
        return null;
    }
}
