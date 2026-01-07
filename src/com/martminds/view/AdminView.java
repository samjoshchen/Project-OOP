package com.martminds.view;

import com.martminds.model.user.User;
import com.martminds.model.order.Order;
import java.util.List;

public class AdminView {

    public void displayAllUsers(List<User> users) {
        if (users == null || users.isEmpty()) {
            System.out.println("\nNo users found.");
            return;
        }

        System.out.println("\nAll Users\n");
        System.out.printf("%-10s %-25s %-30s %-10s\n",
                "ID", "Name", "Email", "Role");
        System.out.println("----------+-------------------------+------------------------------+----------");

        for (User u : users) {
            System.out.printf("%-10s %-25s %-30s %-10s\n",
                    u.getUserId(),
                    u.getName().length() > 25 ? u.getName().substring(0, 22) + "..." : u.getName(),
                    u.getEmail().length() > 30 ? u.getEmail().substring(0, 27) + "..." : u.getEmail(),
                    u.getRole());
        }
        System.out.println();
    }

    public void displayAllOrders(List<Order> orders) {
        if (orders == null || orders.isEmpty()) {
            System.out.println("\nNo orders found.");
            return;
        }

        System.out.println("\nAll Orders\n");
        System.out.printf("%-15s %-15s %-12s %-15s\n",
                "Order ID", "Customer ID", "Total", "Status");
        System.out.println("---------------+---------------+------------+---------------");

        for (Order o : orders) {
            System.out.printf("%-15s %-15s Rp%-10.0f %-15s\n",
                    o.getOrderId().substring(0, Math.min(12, o.getOrderId().length())),
                    o.getCustomerId().substring(0, Math.min(12, o.getCustomerId().length())),
                    o.getTotalPrice(),
                    o.getStatus());
        }
        System.out.println();
    }
}
