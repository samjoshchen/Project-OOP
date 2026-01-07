package com.martminds.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.martminds.model.user.Admin;
import com.martminds.model.user.Customer;
import com.martminds.model.user.Driver;
import com.martminds.model.user.User;
import com.martminds.model.common.Address;
import com.martminds.enums.UserRole;
import com.martminds.util.FileHandler;
import com.martminds.util.Logger;

public class UserService {
    private static UserService instance;
    private List<User> users;
    private static final String USER_FILE = "users.csv";

    private UserService() {
        this.users = new ArrayList<>();
        loadFromFile();
        if (users.isEmpty()) {
            createSampleUsers();
            saveToFile();
        }
    }

    public static UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    private void loadFromFile() {
        List<String> lines = FileHandler.readFile(USER_FILE);

        for (String line : lines) {
            if (line.trim().isEmpty())
                continue;

            try {
                String[] fields = FileHandler.parseCSVLine(line);
                if (fields.length < 7)
                    continue;

                String userId = fields[0];
                String name = fields[1];
                String email = fields[2];
                String password = fields[3];
                String phone = fields[4];
                double balance = Double.parseDouble(fields[5]);
                UserRole role = UserRole.valueOf(fields[6]);

                User user;
                switch (role) {
                    case ADMIN:
                        user = new Admin(userId, name, email, password, phone, balance);
                        break;
                    case CUSTOMER:
                        user = new Customer(userId, name, email, password, phone, balance);
                        break;
                    case DRIVER:
                        user = new Driver(userId, name, email, password, phone, balance);
                        break;
                    default:
                        continue;
                }

                if (fields.length >= 12) {
                    String street = fields[7];
                    String city = fields[8];
                    String postalCode = fields[9];
                    String district = fields[10];
                    String province = fields[11];

                    if (!street.isEmpty()) {
                        Address address = new Address(street, city, postalCode, district, province);
                        user.setAddress(address);
                    }
                }

                users.add(user);
            } catch (Exception e) {
                Logger.error("Error parsing user line: " + line + " - " + e.getMessage());
            }
        }

        Logger.info("Loaded " + users.size() + " users from file");
    }

    private void saveToFile() {
        List<String> lines = new ArrayList<>();

        for (User user : users) {
            Address addr = user.getAddress();
            String street = addr != null ? addr.getStreet() : "";
            String city = addr != null ? addr.getCity() : "";
            String postalCode = addr != null ? addr.getPostalCode() : "";
            String district = addr != null ? addr.getDistrict() : "";
            String province = addr != null ? addr.getProvince() : "";

            String line = FileHandler.formatCSVLine(
                    user.getUserId(),
                    user.getName(),
                    user.getEmail(),
                    user.getPassword(),
                    user.getPhone(),
                    String.valueOf(user.getBalance()),
                    user.getRole().toString(),
                    street,
                    city,
                    postalCode,
                    district,
                    province);
            lines.add(line);
        }

        FileHandler.writeFile(USER_FILE, lines);
    }

    public void registerUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (findUserByEmail(user.getEmail()) != null) {
            throw new IllegalArgumentException("User with email " + user.getEmail() + " already exists");
        }
        users.add(user);
        saveToFile();
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
            saveToFile();
            return true;
        }
        return false;
    }

    public boolean updateUserAddress(String userId, Address newAddress) {
        User user = findUserById(userId);
        if (user != null) {
            user.setAddress(newAddress);
            saveToFile();
            Logger.info("Updated address for user: " + userId);
            return true;
        }
        return false;
    }

    public void updateUser(User user) {
        saveToFile();
    }

    private void createSampleUsers() {
        users.add(new Admin("A001", "Admin One", "admin@mail.com", "admin123", "08111111", 250000));
        users.add(new Customer("C001", "Customer One", "cust1@mail.com", "cust123", "08222222", 500000));
        users.add(new Customer("C002", "Customer Two", "cust2@mail.com", "cust123", "08333333", 400000));
        users.add(new Driver("D001", "Driver One", "driver@mail.com", "driver123", "08444444", 300000));
        users.add(new Driver("D002", "Driver Two", "driver2@mail.com", "driver123", "08555555", 300000));
    }

}
