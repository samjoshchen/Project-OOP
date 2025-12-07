package com.martminds.view;

import java.util.ArrayList;
import java.util.List;

import com.martminds.exception.InvalidOrderException;
import com.martminds.model.common.Address;
import com.martminds.model.order.Order;
import com.martminds.model.order.OrderItem;
import com.martminds.model.product.Product;
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

    public Address getDeliveryAddress() {
        System.out.println("\nEnter Delivery Address\n");
        String street = Input.promptString("Street: ");
        String city = Input.promptString("City: ");
        String postalCode = Input.promptString("Postal Code: ");
        String district = Input.promptString("District: ");
        String province = Input.promptString("Province: ");

        return new Address(street, city, postalCode, district, province);
    }

    public List<OrderItem> collectCartItems() throws InvalidOrderException {
        ProductService productService = ProductService.getInstance();
        List<OrderItem> items = new ArrayList<>();
        System.out.println("\nAdd Items to Cart (type 'done' to finish)\n");

        while (true) {
            String productId = Input.promptString("Product ID: ");
            if (productId.equalsIgnoreCase("done")) {
                break;
            }

            int quantity = Input.promptInt("Quantity: ");

            if (!productService.validateProductAvailability(productId, quantity)) {
                System.out.println("Invalid product or insufficient stock.");
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
