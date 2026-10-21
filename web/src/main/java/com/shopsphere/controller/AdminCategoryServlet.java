package com.shopsphere.controller;

import com.shopsphere.model.Category;
import com.shopsphere.service.AdminCatalogService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

// /admin/categories            -> list + naya category form
// /admin/categories?edit=3     -> wahi page, form mein category 3 bhari hui
@WebServlet("/admin/categories")
public class AdminCategoryServlet extends HttpServlet {

    private final AdminCatalogService catalogService = new AdminCatalogService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Category editCategory = null;
            int editId = parseInt(request.getParameter("edit"), 0);
            if (editId > 0) {
                editCategory = catalogService.getCategory(editId);
            }
            showPage(request, response, editCategory);
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
                Category c = new Category();
                c.setCategoryId(parseInt(request.getParameter("categoryId"), 0));
                c.setName(request.getParameter("name"));
                c.setDescription(request.getParameter("description"));

                String error = catalogService.saveCategory(c);
                if (error != null) {
                    request.setAttribute("error", error);
                    showPage(request, response, c);
                    return;
                }
                session.setAttribute("adminMessage", "Category saved successfully");
                session.setAttribute("adminMessageType", "success");

            } else if ("toggle".equals(action)) {
                boolean enable = "enable".equals(request.getParameter("status"));
                catalogService.setCategoryStatus(parseInt(request.getParameter("categoryId"), 0), enable);
                session.setAttribute("adminMessage", enable ? "Category enabled" : "Category disabled");
                session.setAttribute("adminMessageType", "info");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            session.setAttribute("adminMessage", "Something went wrong. Please try again.");
            session.setAttribute("adminMessageType", "danger");
        }

        response.sendRedirect(request.getContextPath() + "/admin/categories");
    }

    private void showPage(HttpServletRequest request, HttpServletResponse response, Category editCategory)
            throws ServletException, IOException, SQLException {
        request.setAttribute("categories", catalogService.getAllCategories());
        request.setAttribute("editCategory", editCategory);

        HttpSession session = request.getSession();
        Object message = session.getAttribute("adminMessage");
        if (message != null) {
            request.setAttribute("adminMessage", message);
            request.setAttribute("adminMessageType", session.getAttribute("adminMessageType"));
            session.removeAttribute("adminMessage");
            session.removeAttribute("adminMessageType");
        }

        request.getRequestDispatcher("/admin/categories.jsp").forward(request, response);
    }

    private int parseInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException | NullPointerException e) {
            return defaultValue;
        }
    }
}
