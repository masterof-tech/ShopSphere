package com.shopsphere.service;

import com.shopsphere.dao.ProductDAO;
import com.shopsphere.dao.WishlistDAO;
import com.shopsphere.model.Product;

import java.sql.SQLException;
import java.util.List;

public class WishlistService {

    private final WishlistDAO wishlistDAO = new WishlistDAO();
    private final ProductDAO productDAO = new ProductDAO();

    // Sab theek ho to null, warna error message
    public String add(int userId, int productId) throws SQLException {
        Product p = productDAO.getProductById(productId);
        if (p == null || !p.isStatus()) {
            return "Product not found";
        }
        wishlistDAO.add(userId, productId);
        return null;
    }

    public void remove(int userId, int productId) throws SQLException {
        wishlistDAO.remove(userId, productId);
    }

    public boolean isInWishlist(int userId, int productId) throws SQLException {
        return wishlistDAO.exists(userId, productId);
    }

    public List<Product> getProducts(int userId) throws SQLException {
        return wishlistDAO.getProducts(userId);
    }
}
