package com.martminds.model.user;

import java.util.ArrayList;
import java.util.List;
import com.martminds.enums.*;

public class Admin extends User {

    private List<String> adminPermissions;

    public Admin(String userId, String name, String email, String password, String phone, double balance) {
        super(userId, name, email, password, phone, balance, UserRole.ADMIN);
        this.adminPermissions = new ArrayList<>();
    }

    public void addPermission(String permission) {
        adminPermissions.add(permission);
    }

    public void placeOrder(String orderId) {
        System.out.println("Admin placed order: " + orderId);
    }

    public void trackOrder(String orderId) {
        System.out.println("Admin tracking order: " + orderId);
    }

    public void repurchase(String orderId) {
        System.out.println("Admin repurchased order: " + orderId);
    }

    public void viewOrderHistory() {
        System.out.println("Admin viewing order history...");
    }

    public void viewMysteryBoxHistory() {
        System.out.println("Admin viewing mystery box history...");
    }
}
