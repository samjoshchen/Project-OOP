package com.martminds.view;

import java.util.ArrayList;
import java.util.List;

import com.martminds.exception.InvalidOrderException;
import com.martminds.model.common.Address;
import com.martminds.model.order.Order;
import com.martminds.model.order.OrderItem;
import com.martminds.model.product.Product;
import com.martminds.model.user.User;
import com.martminds.service.ProductService;
import com.martminds.util.RandomGenerator;
import com.martminds.util.Input;

public class OrderView {
    public void displayOrderList(List<Order> orders) {
        System.out.println("\nOrders List\n");
        for (Order order : orders) {
            System.out.printf("ID: %s | Status: %s | Total: Rp%.2f | Date: %s\n",
                    order.getOrderId(),
                    order.getStatus(),
                    order.getTotalPrice(),
                    order.getCreatedAt());
        }
        System.out.println();
    }

    public Address selectDeliveryAddress(User currentUser) {
        System.out.println("\nDelivery Address Selection\n");

        Address registeredAddress = currentUser.getAddress();

        if (registeredAddress != null) {
            System.out.println("1. Use Registered Address:");
            System.out.println("   " + registeredAddress.toString());
            System.out.println("2. Use Custom Address");

            while (true) {
                int choice = Input.promptInt("Choose option (1 or 2): ");
                if (choice == 1) {
                    return registeredAddress;
                } else if (choice == 2) {
                    return getCustomDeliveryAddress();
                } else {
                    System.out.println("Invalid option. Please choose 1 or 2.");
                }
            }
        } else {
            System.out.println("No registered address found. Please enter delivery address.\n");
            return getCustomDeliveryAddress();
        }
    }

    public Address getDeliveryAddress() {
        System.out.println("\nEnter Delivery Address (type 'exit' to cancel)\n");
        String street = Input.promptStringWithExit("Street Address (e.g. Jl. Sudirman No. 123): ");
        if ("exit".equals(street))
            return null;

        String city = Input.promptStringWithExit("City (e.g. Jakarta, Bandung): ");
        if ("exit".equals(city))
            return null;

        String postalCode = Input.promptString("Postal Code (5 digits, e.g. 12170): ");
        if ("exit".equals(postalCode))
            return null;
        if (!postalCode.matches("^[0-9]{5}$")) {
            System.out.println("Invalid postal code. Must be exactly 5 digits.");
            return getDeliveryAddress();
        }

        String district = Input.promptStringWithExit("District/Kecamatan (e.g. Kebayoran Baru): ");
        if ("exit".equals(district))
            return null;

        String province = Input.promptStringWithExit("Province (e.g. DKI Jakarta): ");
        if ("exit".equals(province))
            return null;

        return new Address(street, city, postalCode, district, province);
    }

    public Address getCustomDeliveryAddress() {
        while (true) {
            try {
                System.out.println("\nEnter Custom Delivery Address (type 'exit' to cancel)\n");
                String street = Input.promptStringWithExit("Street Address (e.g. Jl. Sudirman No. 123): ");
                if ("exit".equals(street))
                    return null;

                String city = Input.promptStringWithExit("City (e.g. Jakarta, Bandung): ");
                if ("exit".equals(city))
                    return null;

                String postalCode = Input.promptString("Postal Code (5 digits, e.g. 12170): ");
                if ("exit".equals(postalCode))
                    return null;
                if (!postalCode.matches("^[0-9]{5}$")) {
                    System.out.println("Invalid postal code. Must be exactly 5 digits.");
                    continue;
                }

                String district = Input.promptStringWithExit("District/Kecamatan (e.g. Kebayoran Baru): ");
                if ("exit".equals(district))
                    return null;

                String province = Input.promptStringWithExit("Province (e.g. DKI Jakarta): ");
                if ("exit".equals(province))
                    return null;

                return new Address(street, city, postalCode, district, province);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage() + ". Please try again.");
            }
        }
    }

    public List<OrderItem> collectCartItems() throws InvalidOrderException {
        ProductService productService = ProductService.getInstance();
        List<OrderItem> items = new ArrayList<>();
        System.out.println("\nAdd Items to Cart");
        System.out.println("(type 'done' to finish, 'exit' to cancel)\n");

        while (true) {
            String productId = Input.promptProductId("Product ID (e.g. P001, P002, type 'done' to finish): ");
            if (productId.equalsIgnoreCase("done")) {
                break;
            }
            if (productId.equalsIgnoreCase("exit")) {
                return new ArrayList<>();
            }

            int quantity = Input.promptIntPositive("Quantity (min 1, type 'exit' to cancel): ");
            if (quantity == -1) {
                System.out.println("Cart collection cancelled.");
                return new ArrayList<>();
            }

            if (!productService.validateProductAvailability(productId, quantity)) {
                System.out.println("Invalid product or insufficient stock. Please try again.");
                continue;
            }

            Product product = productService.getProductById(productId);

            String orderItemId = RandomGenerator.generateId();
            OrderItem item = new OrderItem(orderItemId,
                    product.getProductId(),
                    product.getName(),
                    quantity,
                    product.getPrice());
            items.add(item);

            System.out.println("Added: " + product.getName() + " x" + quantity);
        }

        return items;
    }
}
