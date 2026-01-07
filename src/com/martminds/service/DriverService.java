package com.martminds.service;

import java.util.*;
import java.util.stream.Collectors;

import com.martminds.enums.OrderStatus;
import com.martminds.exception.InvalidOrderException;
import com.martminds.model.order.Order;
import com.martminds.model.user.Driver;
import com.martminds.model.user.User;

public class DriverService {
    private static DriverService instance;
    private UserService userService;

    private DriverService() {
        this.userService = UserService.getInstance();
    }

    public static DriverService getInstance() {
        if (instance == null) {
            instance = new DriverService();
        }
        return instance;
    }

    public List<Driver> getAllDrivers() {
        return userService.getAllUsers().stream()
                .filter(user -> user instanceof Driver)
                .map(user -> (Driver) user)
                .collect(Collectors.toList());
    }

    public List<Driver> getAvailableDrivers() {
        return getAllDrivers().stream()
                .filter(Driver::isAvailable)
                .collect(Collectors.toList());
    }

    public Driver getDriverById(String driverId) {
        User user = userService.findUserById(driverId);
        if (user instanceof Driver) {
            return (Driver) user;
        }
        return null;
    }

    public Map<String, Integer> getDriverStatistics(String driverId) {
        Map<String, Integer> stats = new HashMap<>();
        Driver driver = getDriverById(driverId);

        if (driver == null) {
            stats.put("total_deliveries", 0);
            stats.put("completed", 0);
            stats.put("in_progress", 0);
            return stats;
        }

        List<Order> allOrders = OrderService.getInstance().getAllOrders();
        List<Order> driverOrders = allOrders.stream()
                .filter(order -> driverId.equals(order.getDriverId()))
                .collect(Collectors.toList());

        int total = driverOrders.size();
        long completed = driverOrders.stream()
                .filter(order -> order.getStatus() == OrderStatus.DELIVERED)
                .count();
        long inProgress = driverOrders.stream()
                .filter(order -> order.getStatus() == OrderStatus.OUT_FOR_DELIVERY)
                .count();

        stats.put("total_deliveries", total);
        stats.put("completed", (int) completed);
        stats.put("in_progress", (int) inProgress);

        return stats;
    }

    public Map<String, Object> getDriverPerformance(String driverId) {
        Map<String, Object> performance = new HashMap<>();
        Map<String, Integer> stats = getDriverStatistics(driverId);

        Driver driver = getDriverById(driverId);
        if (driver == null) {
            performance.put("driver_found", false);
            return performance;
        }

        performance.put("driver_found", true);
        performance.put("driver_name", driver.getName());
        performance.put("total_deliveries", stats.get("total_deliveries"));
        performance.put("completed_deliveries", stats.get("completed"));
        performance.put("in_progress_deliveries", stats.get("in_progress"));
        performance.put("is_available", driver.isAvailable());

        int total = stats.get("total_deliveries");
        int completed = stats.get("completed");
        double completionRate = total > 0 ? (completed * 100.0 / total) : 0.0;
        performance.put("completion_rate", String.format("%.1f%%", completionRate));

        return performance;
    }

    public Driver findBestAvailableDriver() {
        List<Driver> availableDrivers = getAvailableDrivers();

        if (availableDrivers.isEmpty()) {
            return null;
        }

        return availableDrivers.get(0);
    }

    public boolean autoAssignDriver(String orderId) throws InvalidOrderException {
        Driver driver = findBestAvailableDriver();

        if (driver == null) {
            return false;
        }

        Order order = OrderService.getInstance().findOrderById(orderId);
        if (order == null || order.getDriverId() != null) {
            return false;
        }

        order.assignDriver(driver.getUserId());
        driver.acceptOrder(orderId);
        return true;
    }

    public boolean toggleDriverAvailability(String driverId) {
        Driver driver = getDriverById(driverId);
        if (driver == null) {
            return false;
        }

        driver.setAvailable(!driver.isAvailable());
        return true;
    }

    public List<Order> getDriverDeliveries(String driverId) {
        return OrderService.getInstance().getAllOrders().stream()
                .filter(order -> driverId.equals(order.getDriverId()))
                .collect(Collectors.toList());
    }

    public Map<String, Object> getAllDriversStatistics() {
        Map<String, Object> summary = new HashMap<>();
        List<Driver> allDrivers = getAllDrivers();

        summary.put("total_drivers", allDrivers.size());
        summary.put("available_drivers", getAvailableDrivers().size());

        int totalDeliveries = 0;
        for (Driver driver : allDrivers) {
            totalDeliveries += getDriverStatistics(driver.getUserId()).get("total_deliveries");
        }
        summary.put("total_deliveries", totalDeliveries);

        return summary;
    }
}
