package com.shopsphere.controller;

import com.shopsphere.model.Product;
import com.shopsphere.service.AdminCatalogService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

// /admin/products                    -> product list
// /admin/products?action=new         -> naya product form
// /admin/products?action=edit&id=5   -> edit form
@WebServlet("/admin/products")
public class AdminProductServlet extends HttpServlet {

    private final AdminCatalogService catalogService = new AdminCatalogService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String action = request.getParameter("action");

            if ("new".equals(action)) {
                showForm(request, response, new Product());

            } else if ("edit".equals(action)) {
                Product product = catalogService.getProduct(parseInt(request.getParameter("id"), 0));
                if (product == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Product not found");
                    return;
                }
                showForm(request, response, product);

            } else {
                request.setAttribute("products", catalogService.getAllProducts());
                request.setAttribute("categoryNames", catalogService.getCategoryNames());
                moveFlashMessage(request);
                request.getRequestDispatcher("/admin/products.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Something went wrong");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        HttpSession session = request.getSession();

        try {
            if ("save".equals(action)) {
                Product p = new Product();
                p.setProductId(parseInt(request.getParameter("productId"), 0));
                p.setCategoryId(parseInt(request.getParameter("categoryId"), 0));
                p.setName(request.getParameter("name"));
                p.setBrand(request.getParameter("brand"));
                p.setDescription(request.getParameter("description"));
                p.setImageUrl(request.getParameter("imageUrl"));
                p.setPrice(parseDouble(request.getParameter("price"), 0));
                p.setDiscount(blank(request.getParameter("discount")) ? 0 : parseDouble(request.getParameter("discount"), -1));
                p.setStock(parseInt(request.getParameter("stock"), -1));

                String error = catalogService.saveProduct(p);
                if (error != null) {
                    // Galti hui: form wapas dikhao, bhari hui values ke saath
                    request.setAttribute("error", error);
                    showForm(request, response, p);
                    return;
                }
                session.setAttribute("adminMessage", "Product saved successfully");
                session.setAttribute("adminMessageType", "success");

            } else if ("toggle".equals(action)) {
                boolean enable = "enable".equals(request.getParameter("status"));
                catalogService.setProductStatus(parseInt(request.getParameter("productId"), 0), enable);
                session.setAttribute("adminMessage", enable ? "Product enabled" : "Product disabled");
                session.setAttribute("adminMessageType", "info");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            session.setAttribute("adminMessage", "Something went wrong. Please try again.");
            session.setAttribute("adminMessageType", "danger");
        }

        response.sendRedirect(request.getContextPath() + "/admin/products");
    }

    private void showForm(HttpServletRequest request, HttpServletResponse response, Product product)
            throws ServletException, IOException, SQLException {
        request.setAttribute("product", product);
        request.setAttribute("categories", catalogService.getAllCategories());
        request.getRequestDispatcher("/admin/product-form.jsp").forward(request, response);
    }

    private void moveFlashMessage(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Object message = session.getAttribute("adminMessage");
        if (message != null) {
            request.setAttribute("adminMessage", message);
            request.setAttribute("adminMessageType", session.getAttribute("adminMessageType"));
            session.removeAttribute("adminMessage");
            session.removeAttribute("adminMessageType");
        }
    }

    private boolean blank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private int parseInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException | NullPointerException e) {
            return defaultValue;
        }
    }

    private double parseDouble(String value, double defaultValue) {
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException | NullPointerException e) {
            return defaultValue;
        }
    }
}
