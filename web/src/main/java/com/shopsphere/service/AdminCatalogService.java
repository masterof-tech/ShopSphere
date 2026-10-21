package com.shopsphere.service;

import com.shopsphere.dao.CategoryDAO;
import com.shopsphere.dao.ProductDAO;
import com.shopsphere.model.Category;
import com.shopsphere.model.Product;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Admin ke liye products aur categories ka management
public class AdminCatalogService {

    private final ProductDAO productDAO = new ProductDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();

    // ---------- Products ----------

    public List<Product> getAllProducts() throws SQLException {
        return productDAO.getAllProductsForAdmin();
    }

    public Product getProduct(int id) throws SQLException {
        return productDAO.getProductById(id);
    }

    // Sab theek ho to null, warna error message. productId > 0 matlab edit, 0 matlab naya product.
    public String saveProduct(Product p) throws SQLException {
        String name = p.getName() == null ? "" : p.getName().trim();
        String brand = p.getBrand() == null ? "" : p.getBrand().trim();
        String description = p.getDescription() == null ? "" : p.getDescription().trim();
        String image = p.getImageUrl() == null ? "" : p.getImageUrl().trim();

        if (name.isEmpty()) {
            return "Product name is required";
        }
        if (name.length() > 150) {
            return "Product name must be 150 characters or less";
        }
        if (brand.length() > 80) {
            return "Brand must be 80 characters or less";
        }
        if (categoryDAO.getCategoryById(p.getCategoryId()) == null) {
            return "Please select a category";
        }
        if (p.getPrice() <= 0) {
            return "Please enter a valid price greater than 0";
        }
        if (p.getDiscount() < 0 || p.getDiscount() > 100) {
            return "Discount must be a number between 0 and 100";
        }
        if (p.getStock() < 0) {
            return "Stock must be a whole number (0 or more)";
        }
        if (image.length() > 255) {
            return "Image path must be 255 characters or less";
        }
        // Image path page ke andar src mein jata hai, isliye sirf safe characters allow
        if (!image.isEmpty() && (!image.matches("[A-Za-z0-9_./-]+") || image.contains(".."))) {
            return "Image path can contain only letters, numbers, dots, dashes, underscores and slashes";
        }

        p.setName(name);
        p.setBrand(brand);
        p.setDescription(description);
        p.setImageUrl(image);

        if (p.getProductId() > 0) {
            if (productDAO.getProductById(p.getProductId()) == null) {
                return "Product not found";
            }
            productDAO.updateProduct(p);
        } else {
            productDAO.insertProduct(p);
        }
        return null;
    }

    public void setProductStatus(int productId, boolean status) throws SQLException {
        productDAO.setProductStatus(productId, status);
    }

    // ---------- Categories ----------

    public List<Category> getAllCategories() throws SQLException {
        return categoryDAO.getAllCategoriesForAdmin();
    }

    public Category getCategory(int id) throws SQLException {
        return categoryDAO.getCategoryById(id);
    }

    // category id -> naam (product list mein category ka naam dikhane ke liye)
    public Map<Integer, String> getCategoryNames() throws SQLException {
        Map<Integer, String> names = new LinkedHashMap<>();
        for (Category c : categoryDAO.getAllCategoriesForAdmin()) {
            names.put(c.getCategoryId(), c.getName());
        }
        return names;
    }

    // Sab theek ho to null, warna error message. categoryId > 0 matlab edit, 0 matlab nayi category.
    public String saveCategory(Category c) throws SQLException {
        String name = c.getName() == null ? "" : c.getName().trim();
        String description = c.getDescription() == null ? "" : c.getDescription().trim();

        if (name.isEmpty()) {
            return "Category name is required";
        }
        if (name.length() > 80) {
            return "Category name must be 80 characters or less";
        }
        if (description.length() > 255) {
            return "Description must be 255 characters or less";
        }
        if (categoryDAO.nameExists(name, c.getCategoryId())) {
            return "A category with this name already exists";
        }

        c.setName(name);
        c.setDescription(description);

        if (c.getCategoryId() > 0) {
            if (categoryDAO.getCategoryById(c.getCategoryId()) == null) {
                return "Category not found";
            }
            categoryDAO.updateCategory(c);
        } else {
            categoryDAO.insertCategory(c);
        }
        return null;
    }

    public void setCategoryStatus(int categoryId, boolean status) throws SQLException {
        categoryDAO.setCategoryStatus(categoryId, status);
    }
}
