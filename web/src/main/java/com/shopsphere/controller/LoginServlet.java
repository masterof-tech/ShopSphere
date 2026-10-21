package com.shopsphere.controller;

import com.shopsphere.model.User;
import com.shopsphere.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try {
            User user = userService.login(email, password);

            if (user == null) {
                request.setAttribute("error", "Email ya password galat hai");
                request.getRequestDispatcher("/login.jsp").forward(request, response);
                return;
            }

            // Purana session hatao, naya banao (session fixation se bachne ke liye)
            HttpSession old = request.getSession(false);
            if (old != null) {
                old.invalidate();
            }
            HttpSession session = request.getSession(true);
            session.setAttribute("user", user);
            session.setAttribute("role", user.getRole());

            response.sendRedirect(request.getContextPath() + "/index.jsp");

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Server error aaya, thodi der baad try karo");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }
}
