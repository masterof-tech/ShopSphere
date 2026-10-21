package com.shopsphere.model;

// Cart ki ek row + us product ki zaroori details (join ke baad)
public class CartItem {
    private int cartId;
    private int userId;
    private int productId;
    private int quantity;

    private String productName;
    private String brand;
    private String imageUrl;
    private double price;
    private double discount;   // percent
    private int stock;

    public CartItem() {}

    public int getCartId() { return cartId; }
    public void setCartId(int cartId) { this.cartId = cartId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public double getDiscount() { return discount; }
    public void setDiscount(double discount) { this.discount = discount; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    // Discount ke baad ek piece ka price
    public double getUnitPrice() {
        return price - (price * discount / 100);
    }

    // unit price x quantity
    public double getSubtotal() {
        return getUnitPrice() * quantity;
    }
}
