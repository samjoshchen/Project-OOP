package com.martminds.controller;

import java.util.List;
import java.util.stream.Collectors;

import com.martminds.enums.OrderStatus;
import com.martminds.exception.InvalidOrderException;
import com.martminds.model.order.Order;
import com.martminds.model.user.Driver;
import com.martminds.model.user.User;
import com.martminds.service.OrderService;
import com.martminds.util.Session;
import com.martminds.util.ValidationUtil;

public class DriverController {

    public List<Order> getAvailableOrders() {
        Session.getInstance().requireDriver();

        return OrderService.getInstance().getAllOrders().stream()
                .filter(order -> order.getStatus() == OrderStatus.READY_FOR_PICKUP)
                .filter(order -> order.getDriverId() == null)
                .collect(Collectors.toList());
    }

    public boolean acceptOrder(String orderId) throws InvalidOrderException {
        Session.getInstance().requireDriver();

        if (!ValidationUtil.isNotEmpty(orderId)) {
            throw new IllegalArgumentException("Order ID cannot be empty");
        }

        User currentUser = Session.getInstance().getCurrentUser();
        Driver driver = (Driver) currentUser;

        Order order = OrderService.getInstance().findOrderById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Order not found: " + orderId);
        }

        if (order.getStatus() != OrderStatus.READY_FOR_PICKUP) {
            throw new InvalidOrderException("Order is not ready for pickup", orderId);
        }

        if (order.getDriverId() != null) {
            throw new InvalidOrderException("Order already assigned to another driver", orderId);
        }

        order.assignDriver(driver.getUserId());
        driver.acceptOrder(orderId);

        return true;
    }

    public boolean updateDeliveryStatus(String orderId, OrderStatus newStatus) throws InvalidOrderException {
        Session.getInstance().requireDriver();

        if (!ValidationUtil.isNotEmpty(orderId)) {
            throw new IllegalArgumentException("Order ID cannot be empty");
        }

        User currentUser = Session.getInstance().getCurrentUser();
        Driver driver = (Driver) currentUser;

        Order order = OrderService.getInstance().findOrderById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Order not found: " + orderId);
        }

        if (!driver.getUserId().equals(order.getDriverId())) {
            throw new InvalidOrderException("You are not assigned to this order", orderId);
        }

        order.updateStatus(newStatus);

        if (newStatus == OrderStatus.DELIVERED) {
            order.markAsDelivered();
        }

        return true;
    }

    public List<Order> getMyDeliveries() {
        Session.getInstance().requireDriver();

        User currentUser = Session.getInstance().getCurrentUser();
        Driver driver = (Driver) currentUser;

        return OrderService.getInstance().getAllOrders().stream()
                .filter(order -> driver.getUserId().equals(order.getDriverId()))
                .collect(Collectors.toList());
    }

    public boolean updateLocation(double latitude, double longitude) {
        Session.getInstance().requireDriver();

        User currentUser = Session.getInstance().getCurrentUser();
        Driver driver = (Driver) currentUser;

        driver.updateLocation(latitude, longitude);
        return true;
    }
}
