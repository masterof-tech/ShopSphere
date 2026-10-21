package com.shopsphere.service;

import com.shopsphere.dao.UserDAO;
import com.shopsphere.model.User;
import com.shopsphere.util.PasswordUtil;

import java.sql.SQLException;

public class UserService {

    private final UserDAO userDAO = new UserDAO();

    // Register: sab theek ho to null return karta hai, warna error message
    public String register(String name, String email, String mobile,
                           String password, String confirmPassword) throws SQLException {

        name = name == null ? "" : name.trim();
        email = email == null ? "" : email.trim().toLowerCase();
        mobile = mobile == null ? "" : mobile.trim();
        password = password == null ? "" : password;

        if (name.isEmpty()) {
            return "Naam likhna zaroori hai";
        }
        if (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            return "Sahi email likho";
        }
        if (!mobile.matches("\\d{10}")) {
            return "Mobile number 10 digit ka hona chahiye";
        }
        if (password.length() < 6) {
            return "Password kam se kam 6 character ka hona chahiye";
        }
        if (!password.equals(confirmPassword)) {
            return "Dono password same nahi hain";
        }
        if (userDAO.emailExists(email)) {
            return "Ye email pehle se registered hai";
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setMobile(mobile);
        user.setPassword(PasswordUtil.hash(password));   // hash karke save
        userDAO.registerUser(user);
        return null;
    }

    // Login: sahi ho to User, warna null
    public User login(String email, String password) throws SQLException {
        if (email == null || password == null) {
            return null;
        }
        return userDAO.login(email.trim().toLowerCase(), PasswordUtil.hash(password));
    }
}
