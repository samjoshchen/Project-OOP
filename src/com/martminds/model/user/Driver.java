package com.martminds.model.user;

import java.util.ArrayList;
import java.util.List;
import com.martminds.enums.*;

public class Driver extends User {
    private boolean isAvailable;
    private List<String> deliveryHistory;
    private double latitude;
    private double longitude;

    public Driver(String userId, String name, String email, String password, String phone, double balance) {
        super(userId, name, email, password, phone, balance, UserRole.DRIVER);
        this.isAvailable = true;
        this.deliveryHistory = new ArrayList<>();
        this.latitude = 0.0;
        this.longitude = 0.0;
    }

    public void acceptOrder(String orderId) {
        deliveryHistory.add(orderId);
        isAvailable = false;
    }

    public void updateDeliveryStatus(String orderId, String status) {
        System.out.println("Order " + orderId + " status updated to: " + status);
    }

    public void updateLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
