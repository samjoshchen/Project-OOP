package com.martminds.model.user;

import java.util.ArrayList;
import java.util.List;
import com.martminds.enums.*;

public class Customer extends User {

    private List<String> orderHistory;
    private List<String> mysteryBoxHistory;

    public Customer(String userId, String name, String email, String password, String phone, double balance) {
        super(userId, name, email, password, phone, balance, UserRole.CUSTOMER);
        this.orderHistory = new ArrayList<>();
        this.mysteryBoxHistory = new ArrayList<>();
    }

    public void placeOrder(String orderId) {
        orderHistory.add(orderId);
    }

    public void addMysteryBoxOrder(String orderId) {
        mysteryBoxHistory.add(orderId);
    }

    public void trackOrder(String orderId) {
        System.out.println("Tracking order: " + orderId);
    }

    public void repurchase(String orderId) {
        orderHistory.add(orderId);
        System.out.println("Repurchased order: " + orderId);
    }

    public List<String> getOrderHistory() {
        return new ArrayList<>(orderHistory);
    }

    public List<String> getMysteryBoxHistory() {
        return new ArrayList<>(mysteryBoxHistory);
    }

    public void viewOrderHistory() {
        System.out.println("Order History: " + orderHistory);
    }

    public void viewMysteryBoxHistory() {
        System.out.println("Mystery Box History: " + mysteryBoxHistory);
    }
}
