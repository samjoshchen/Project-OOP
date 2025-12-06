package com.martminds.controller;

import com.martminds.model.user.Admin;
import com.martminds.model.user.Customer;
import com.martminds.model.user.Driver;
import com.martminds.model.user.User;
import com.martminds.enums.UserRole;
import com.martminds.service.UserService;
import com.martminds.util.Session;
import com.martminds.util.ValidationUtil;

public class AuthController {

    public User register(String id, String name, String email, String password, String phone, UserRole role,
            double balance) {
        // Validate inputs
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (!ValidationUtil.isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }
        if (!ValidationUtil.isValidPhone(phone)) {
            throw new IllegalArgumentException("Invalid phone number format");
        }
        if (balance < 0) {
            throw new IllegalArgumentException("Balance cannot be negative");
        }

        // Check if user already exists
        if (UserService.getInstance().findUserByEmail(email) != null) {
            throw new IllegalArgumentException("User with email " + email + " already exists");
        }
        if (UserService.getInstance().findUserById(id) != null) {
            throw new IllegalArgumentException("User with ID " + id + " already exists");
        }

        User newUser;
        switch (role) {
            case ADMIN:
                newUser = new Admin(id, name, email, password, phone, balance);
                break;
            case CUSTOMER:
                newUser = new Customer(id, name, email, password, phone, balance);
                break;
            case DRIVER:
                newUser = new Driver(id, name, email, password, phone, balance);
                break;
            default:
                throw new IllegalArgumentException("Unknown role: " + role);
        }

        UserService.getInstance().registerUser(newUser);
        return newUser;
    }

    public User login(String email, String password) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        // Check if already logged in
        if (Session.getInstance().isLoggedIn()) {
            throw new IllegalStateException("User already logged in. Please logout first.");
        }

        User user = UserService.getInstance().authenticate(email, password);
        if (user == null) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        Session.getInstance().login(user);
        return user;
    }

    public void logout() {
        if (!Session.getInstance().isLoggedIn()) {
            throw new IllegalStateException("No user is currently logged in");
        }
        Session.getInstance().logout();
    }

    public User getCurrentUser() {
        return Session.getInstance().getCurrentUser();
    }

    public boolean isLoggedIn() {
        return Session.getInstance().isLoggedIn();
    }
}
