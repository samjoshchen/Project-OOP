package com.martminds.view;

import java.util.List;

import com.martminds.model.order.Order;
import com.martminds.util.Input;

public class DriverView {

    public void displayAvailableOrders(List<Order> orders) {
        System.out.println("\nAvailable Orders for Pickup\n");

        if (orders == null || orders.isEmpty()) {
            System.out.println("\nNo available orders at the moment.");
            return;
        }

        System.out.println();
        for (Order order : orders) {
            System.out.printf("Order ID      : %s\n", order.getOrderId());
            System.out.printf("Store ID      : %s\n", order.getStoreId());
            System.out.printf("Total Amount  : Rp %,.0f\n", order.getTotalPrice());
            System.out.printf("Items Count   : %d\n", order.getItemCount());
            System.out.printf("Delivery To   : %s\n\n", order.getDeliveryAddress().getCity());
        }
        System.out.println();
    }

    public void displayMyDeliveries(List<Order> orders) {
        System.out.println("\nMy Active Deliveries\n");

        if (orders == null || orders.isEmpty()) {
            System.out.println("\nYou have no active deliveries.");
            return;
        }

        System.out.println();
        for (Order order : orders) {
            System.out.printf("Order ID      : %s\n", order.getOrderId());
            System.out.printf("Status        : %s\n", order.getStatus());
            System.out.printf("Total Amount  : Rp %,.0f\n", order.getTotalPrice());
            String fullAddress = order.getDeliveryAddress().getFullAddress();
            System.out.printf("Delivery To   : %s\n\n", fullAddress);
        }
        System.out.println();
    }

    public String selectOrderToAccept() {
        return Input.promptOrderId("\nEnter Order ID to accept (e.g. ORD001, or 'cancel'): ");
    }

    public String selectOrderToUpdate() {
        return Input.promptOrderId("\nEnter Order ID to update (e.g. ORD001, or 'cancel'): ");
    }

    public int selectDeliveryStatus() {
        System.out.println("\nSelect New Status:");
        System.out.println("1. Out for Delivery");
        System.out.println("2. Mark as Delivered");
        System.out.println("3. Cancel");
        System.out.println();
        return Input.promptInt("Choose option (1-3): ");
    }

    public int displayDriverMenu() {
        System.out.println("\nDriver Menu\n");
        System.out.println("1. View Available Orders");
        System.out.println("2. My Active Deliveries");
        System.out.println("3. Accept Order");
        System.out.println("4. Update Delivery Status");
        System.out.println("5. View Profile");
        System.out.println("6. Logout");
        System.out.println();
        return Input.promptInt("Choose option (1-6): ");
    }
}
