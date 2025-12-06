package com.martminds.model.store;

import com.martminds.model.common.Address;
import com.martminds.model.order.Order;
import com.martminds.enums.OrderStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Store {
    private String storeId;
    private String name;
    private Address address;
    private String contactNumber;
    private double rating;
    private List<Order> orders;

    public Store(String storeId, String name, Address address, String contactNumber) {
        this.storeId = storeId;
        this.name = name;
        this.address = address;
        this.contactNumber = contactNumber;
        this.rating = 0.0;
        this.orders = new ArrayList<>();
    }

    public String getStoreId() { return storeId; }
    public String getName() { return name; }
    public Address getAddress() { return address; }
    public String getContactNumber() { return contactNumber; }
    public double getRating() { return rating; }

    public void setName(String name) { this.name = name; }
    public void setAddress(Address address) { this.address = address; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    public void setRating(double rating) { 
        if (rating >= 0.0 && rating <= 5.0) {
            this.rating = rating;
        }
    }

    public List<Order> getActiveOrders() {
        return orders.stream()
            .filter(order -> order.getStatus() != OrderStatus.DELIVERED && order.getStatus() != OrderStatus.CANCELLED)
            .collect(Collectors.toList());
    }

    public void manageOrder(Order order, OrderStatus newStatus) {
        if (order != null && orders.contains(order)) {
            try {
                order.updateStatus(newStatus);
            } catch (Exception e) {
                System.out.println("Something went wrong while updating order status: " + e.getMessage());
            }
        }
    }

    public void addOrder(Order order) {
        if (order != null && !orders.contains(order)) {
            orders.add(order);
        }
    }

    public List<Order> getAllOrders() {
        return new ArrayList<>(orders);
    }

    @Override
    public String toString() {
        return String.format("Store[ID=%s, Name=%s, Contact=%s, Rating=%.1f]",
            storeId, name, contactNumber, rating);
    }
}
