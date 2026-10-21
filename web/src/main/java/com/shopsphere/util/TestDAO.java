package com.shopsphere.util;

import com.shopsphere.dao.CategoryDAO;
import com.shopsphere.dao.ProductDAO;
import com.shopsphere.dao.UserDAO;
import com.shopsphere.model.Category;
import com.shopsphere.model.Product;
import com.shopsphere.model.User;

// Sirf check karne ke liye: DAO classes sahi chal rahi hain ya nahi
public class TestDAO {
    public static void main(String[] args) {
        try {
            CategoryDAO categoryDAO = new CategoryDAO();
            ProductDAO productDAO = new ProductDAO();
            UserDAO userDAO = new UserDAO();

            System.out.println("--- Categories ---");
            for (Category c : categoryDAO.getAllCategories()) {
                System.out.println(c.getCategoryId() + " | " + c.getName());
            }

            System.out.println("--- Products ---");
            for (Product p : productDAO.getAllProducts()) {
                System.out.println(p.getProductId() + " | " + p.getName()
                        + " | Rs." + p.getPrice() + " | final Rs." + p.getFinalPrice());
            }

            System.out.println("--- Search 'phone' ---");
            for (Product p : productDAO.searchProducts("phone")) {
                System.out.println(p.getName());
            }

            System.out.println("--- Login test ---");
            User ok = userDAO.login("admin@shopsphere.com", PasswordUtil.hash("admin123"));
            System.out.println("Sahi password: " + (ok != null ? "Login OK, role = " + ok.getRole() : "FAIL"));

            User bad = userDAO.login("admin@shopsphere.com", PasswordUtil.hash("galat"));
            System.out.println("Galat password: " + (bad == null ? "Login blocked (sahi hai)" : "Problem!"));

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
