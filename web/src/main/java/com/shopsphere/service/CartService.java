package com.shopsphere.service;

import com.shopsphere.dao.CartDAO;
import com.shopsphere.dao.ProductDAO;
import com.shopsphere.model.CartItem;
import com.shopsphere.model.Product;

import java.sql.SQLException;
import java.util.List;

public class CartService {

    private final CartDAO cartDAO = new CartDAO();
    private final ProductDAO productDAO = new ProductDAO();

    // Sab theek ho to null, warna user ko dikhane wala message
    public String addToCart(int userId, int productId, int quantity) throws SQLException {
        if (quantity < 1) {
            return "Quantity must be at least 1";
        }

        Product product = productDAO.getProductById(productId);
        if (product == null || !product.isStatus()) {
            return "Product not found";
        }
        if (product.getStock() < 1) {
            return "Sorry, this product is out of stock";
        }

        int current = cartDAO.getQuantity(userId, productId);
        int newQty = current + quantity;
        String message = null;

        if (newQty > product.getStock()) {
            newQty = product.getStock();
            message = "Only " + product.getStock() + " units are available, so the quantity was adjusted";
        }

        if (current == 0) {
            cartDAO.addItem(userId, productId, newQty);
        } else {
            cartDAO.setQuantity(userId, productId, newQty);
        }
        return message;
    }

    public String updateQuantity(int userId, int productId, int quantity) throws SQLException {
        if (cartDAO.getQuantity(userId, productId) == 0) {
            return "That item is not in your cart";
        }
        if (quantity < 1) {
            cartDAO.removeItem(userId, productId);
            return null;
        }

        Product product = productDAO.getProductById(productId);
        if (product == null || !product.isStatus() || product.getStock() < 1) {
            cartDAO.removeItem(userId, productId);
            return "This product is no longer available, so it was removed from your cart";
        }

        if (quantity > product.getStock()) {
            cartDAO.setQuantity(userId, productId, product.getStock());
            return "Only " + product.getStock() + " units are available, so the quantity was adjusted";
        }

        cartDAO.setQuantity(userId, productId, quantity);
        return null;
    }

    public void removeItem(int userId, int productId) throws SQLException {
        cartDAO.removeItem(userId, productId);
    }

    public List<CartItem> getCartItems(int userId) throws SQLException {
        return cartDAO.getCartItems(userId);
    }

    public double getTotal(List<CartItem> items) {
        double total = 0;
        for (CartItem item : items) {
            total += item.getSubtotal();
        }
        return total;
    }
}
