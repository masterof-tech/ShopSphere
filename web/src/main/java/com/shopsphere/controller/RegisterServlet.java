package com.shopsphere.controller;

import com.shopsphere.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private final UserService userService = new UserService();

    // Form dikhao
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }

    // Form submit hone par
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        try {
            String error = userService.register(
                    request.getParameter("name"),
                    request.getParameter("email"),
                    request.getParameter("mobile"),
                    request.getParameter("password"),
                    request.getParameter("confirmPassword"));

            if (error == null) {
                response.sendRedirect(request.getContextPath() + "/login?registered=1");
            } else {
                request.setAttribute("error", error);
                request.getRequestDispatcher("/register.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Server error aaya, thodi der baad try karo");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
        }
    }
}
