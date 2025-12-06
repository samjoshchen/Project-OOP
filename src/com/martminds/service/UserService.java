package com.martminds.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.martminds.model.user.Admin;
import com.martminds.model.user.Customer;
import com.martminds.model.user.Driver;
import com.martminds.model.user.User;
import com.martminds.enums.UserRole;

public class UserService {
    private static UserService instance;
    private List<User> users;

    private UserService() {
        this.users = new ArrayList<>();
        createSampleUsers();
    }

    public static UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    public void registerUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (findUserByEmail(user.getEmail()) != null) {
            throw new IllegalArgumentException("User with email " + user.getEmail() + " already exists");
        }
        users.add(user);
    }

    public User authenticate(String email, String password) {
        if (email == null || password == null) {
            return null;
        }
        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(email) && u.getPassword().equals(password)) {
                return u;
            }
        }
        return null;
    }

    public User findUserById(String id) {
        if (id == null) {
            return null;
        }
        for (User u : users) {
            if (u.getUserId().equals(id)) {
                return u;
            }
        }
        return null;
    }

    public User findUserByEmail(String email) {
        if (email == null) {
            return null;
        }
        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                return u;
            }
        }
        return null;
    }

    public List<Customer> getAllCustomers() {
        return users.stream()
                .filter(u -> u.getRole() == UserRole.CUSTOMER)
                .map(u -> (Customer) u)
                .collect(Collectors.toList());
    }

    public List<Driver> getAllDrivers() {
        return users.stream()
                .filter(u -> u.getRole() == UserRole.DRIVER)
                .map(u -> (Driver) u)
                .collect(Collectors.toList());
    }

    public List<Admin> getAllAdmins() {
        return users.stream()
                .filter(u -> u.getRole() == UserRole.ADMIN)
                .map(u -> (Admin) u)
                .collect(Collectors.toList());
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    public boolean updateUserBalance(String userId, double newBalance) {
        User user = findUserById(userId);
        if (user != null) {
            user.setBalance(newBalance);
            return true;
        }
        return false;
    }

    private void createSampleUsers() {
        users.add(new Admin("A001", "Admin One", "admin@mail.com", "admin123", "08111111", 250));
        users.add(new Customer("C001", "Customer One", "cust1@mail.com", "cust123", "08222222", 500));
        users.add(new Customer("C002", "Customer Two", "cust2@mail.com", "cust123", "08333333", 400));
        users.add(new Driver("D001", "Driver One", "driver@mail.com", "driver123", "08444444", 300));
        users.add(new Driver("D002", "Driver Two", "driver2@mail.com", "driver123", "08555555", 300));
    }

}
