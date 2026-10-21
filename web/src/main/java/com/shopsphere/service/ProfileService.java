package com.shopsphere.service;

import com.shopsphere.dao.AddressDAO;
import com.shopsphere.dao.AdminUserDAO;
import com.shopsphere.dao.UserDAO;
import com.shopsphere.model.Address;
import com.shopsphere.model.User;
import com.shopsphere.util.PasswordUtil;

import java.sql.SQLException;
import java.util.List;

public class ProfileService {

    private final AdminUserDAO userReader = new AdminUserDAO();   // user ko bina password ke padhta hai
    private final UserDAO userDAO = new UserDAO();
    private final AddressDAO addressDAO = new AddressDAO();
    private final CheckoutService checkoutService = new CheckoutService();

    public User getUser(int userId) throws SQLException {
        return userReader.getUserById(userId);
    }

    public List<Address> getAddresses(int userId) throws SQLException {
        return addressDAO.getAddressesByUser(userId);
    }

    // Sab theek ho to null, warna error message
    public String updateProfile(int userId, String name, String mobile) throws SQLException {
        name = name == null ? "" : name.trim();
        mobile = mobile == null ? "" : mobile.trim();

        if (name.isEmpty()) {
            return "Name is required";
        }
        if (name.length() > 100) {
            return "Name must be 100 characters or less";
        }
        if (!mobile.matches("\\d{10}")) {
            return "Mobile number must be 10 digits";
        }
        userDAO.updateProfile(userId, name, mobile);
        return null;
    }

    // Purana password check karke naya set karo. Sab theek ho to null, warna error message.
    public String changePassword(User user, String current, String newPassword, String confirm)
            throws SQLException {

        if (current == null || current.isEmpty()) {
            return "Please enter your current password";
        }
        // Purana password sahi hai? (login wali query hi use kar rahe hain)
        if (userDAO.login(user.getEmail(), PasswordUtil.hash(current)) == null) {
            return "Current password is incorrect";
        }
        if (newPassword == null || newPassword.length() < 6) {
            return "New password must be at least 6 characters";
        }
        if (!newPassword.equals(confirm)) {
            return "New passwords do not match";
        }
        if (newPassword.equals(current)) {
            return "New password must be different from the current password";
        }
        userDAO.updatePassword(user.getUserId(), PasswordUtil.hash(newPassword));
        return null;
    }

    // Checkout wali validation hi yahan bhi use hoti hai
    public String addAddress(int userId, String line, String city, String state,
                             String pincode, String type) throws SQLException {
        return checkoutService.addAddress(userId, line, city, state, pincode, type);
    }

    public String deleteAddress(int userId, int addressId) throws SQLException {
        Address address = addressDAO.getAddress(addressId, userId);   // sirf apna address
        if (address == null) {
            return "Address not found";
        }
        if (addressDAO.isUsedInOrder(addressId)) {
            return "This address is used in an order, so it cannot be deleted";
        }
        addressDAO.deleteAddress(addressId, userId);
        return null;
    }
}
