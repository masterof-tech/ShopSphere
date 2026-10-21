package com.shopsphere.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

// Sirf check karne ke liye: database se connection chalta hai ya nahi
public class TestConnection {
    public static void main(String[] args) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM products");
             ResultSet rs = ps.executeQuery()) {

            System.out.println("Database connected!");
            if (rs.next()) {
                System.out.println("Products in database: " + rs.getInt(1));
            }
        } catch (Exception e) {
            System.out.println("Connection FAIL: " + e.getMessage());
        }
    }
}
