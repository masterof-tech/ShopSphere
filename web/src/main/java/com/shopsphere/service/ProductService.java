package com.shopsphere.service;

import com.shopsphere.dao.CategoryDAO;
import com.shopsphere.dao.ProductDAO;
import com.shopsphere.model.Category;
import com.shopsphere.model.Product;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ProductService {

    private final ProductDAO productDAO = new ProductDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();

    // keyword (optional) + category (0 = all) + sort (optional)
    public List<Product> getProducts(int categoryId, String keyword, String sort) throws SQLException {
        List<Product> list;

        if (keyword != null && !keyword.trim().isEmpty()) {
            list = productDAO.searchProducts(keyword.trim());
        } else {
            list = productDAO.getAllProducts();
        }

        if (categoryId > 0) {
            list = list.stream()
                    .filter(p -> p.getCategoryId() == categoryId)
                    .collect(Collectors.toList());
        }

        if ("price_asc".equals(sort)) {
            list.sort(Comparator.comparingDouble(Product::getFinalPrice));
        } else if ("price_desc".equals(sort)) {
            list.sort(Comparator.comparingDouble(Product::getFinalPrice).reversed());
        } else if ("name".equals(sort)) {
            list.sort(Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER));
        }

        return list;
    }

    // Sirf active product return hota hai, warna null
    public Product getProduct(int id) throws SQLException {
        Product p = productDAO.getProductById(id);
        return (p != null && p.isStatus()) ? p : null;
    }

    public List<Category> getCategories() throws SQLException {
        return categoryDAO.getAllCategories();
    }

    public Category getCategory(int id) throws SQLException {
        return categoryDAO.getCategoryById(id);
    }
}
