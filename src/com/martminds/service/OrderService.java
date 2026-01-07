package com.martminds.service;

import java.util.ArrayList;
import java.util.List;
import com.martminds.model.order.Order;
import com.martminds.model.order.OrderItem;
import com.martminds.model.common.Address;
import com.martminds.enums.OrderStatus;
import com.martminds.model.product.Product;
import com.martminds.exception.InvalidOrderException;
import com.martminds.exception.OutOfStockException;
import com.martminds.util.FileHandler;
import com.martminds.util.Logger;

public class OrderService {
    private static OrderService instance;
    private final ProductService productService = ProductService.getInstance();
    private List<Order> orders;
    private static final String ORDER_FILE = "orders.csv";
    private static final String ORDER_ITEMS_FILE = "order_items.csv";

    private OrderService() {
        this.orders = new ArrayList<>();
        loadFromFile();
    }

    public static OrderService getInstance() {
        if (instance == null) {
            instance = new OrderService();
        }
        return instance;
    }

    private void loadFromFile() {
        List<String> orderLines = FileHandler.readFile(ORDER_FILE);
        List<String> itemLines = FileHandler.readFile(ORDER_ITEMS_FILE);

        for (String line : orderLines) {
            if (line.trim().isEmpty())
                continue;

            try {
                String[] fields = FileHandler.parseCSVLine(line);
                if (fields.length < 10)
                    continue;

                String orderId = fields[0];
                String customerId = fields[1];
                String storeId = fields[2];
                OrderStatus status = OrderStatus.valueOf(fields[3]);

                String street = fields[5];
                String city = fields[6];
                String postalCode = fields[7];
                String district = fields[8];
                String province = fields[9];
                Address address = new Address(street, city, postalCode, district, province);

                Order order = new Order(orderId, customerId, storeId, address);
                order.updateStatus(status);

                orders.add(order);
            } catch (Exception e) {
                Logger.error("Error parsing order line: " + line + " - " + e.getMessage());
            }
        }

        for (String line : itemLines) {
            if (line.trim().isEmpty())
                continue;

            try {
                String[] fields = FileHandler.parseCSVLine(line);
                if (fields.length < 6)
                    continue;

                String orderId = fields[0];
                String itemId = fields[1];
                String productId = fields[2];
                String productName = fields[3];
                int quantity = Integer.parseInt(fields[4]);
                double price = Double.parseDouble(fields[5]);

                Order order = findOrderById(orderId);
                if (order != null) {
                    OrderItem item = new OrderItem(itemId, productId, productName, quantity, price);
                    order.addItem(item);
                }
            } catch (Exception e) {
                Logger.error("Error parsing order item line: " + line + " - " + e.getMessage());
            }
        }

        Logger.info("Loaded " + orders.size() + " orders from file");
    }

    private void saveToFile() {
        List<String> orderLines = new ArrayList<>();
        List<String> itemLines = new ArrayList<>();

        for (Order order : orders) {
            Address addr = order.getDeliveryAddress();
            String orderLine = FileHandler.formatCSVLine(
                    order.getOrderId(),
                    order.getCustomerId(),
                    order.getStoreId(),
                    order.getStatus().toString(),
                    order.getCreatedAt().toString(),
                    addr.getStreet(),
                    addr.getCity(),
                    addr.getPostalCode(),
                    addr.getDistrict(),
                    addr.getProvince());
            orderLines.add(orderLine);

            for (OrderItem item : order.getItems()) {
                String itemLine = FileHandler.formatCSVLine(
                        order.getOrderId(),
                        item.getOrderItemId(),
                        item.getProductId(),
                        item.getProductName(),
                        String.valueOf(item.getQuantity()),
                        String.valueOf(item.getPriceAtPurchase()));
                itemLines.add(itemLine);
            }
        }

        FileHandler.writeFile(ORDER_FILE, orderLines);
        FileHandler.writeFile(ORDER_ITEMS_FILE, itemLines);
    }

    public Order createOrder(Order order) throws OutOfStockException {
        for (OrderItem item : order.getItems()) {
            Product product = productService.getProductById(item.getProductId());
            if (product == null || product.getStock() < item.getQuantity()) {
                throw new OutOfStockException(
                        "Product " + (product != null ? product.getName() : item.getProductId()) + " is out of stock!");
            }
        }

        for (OrderItem item : order.getItems()) {
            Product product = productService.getProductById(item.getProductId());
            product.updateStock(-item.getQuantity());
        }

        orders.add(order);
        saveToFile();
        return order;
    }

    public Order findOrderById(String id) {
        for (Order o : orders) {
            if (o.getOrderId().equals(id)) {
                return o;
            }
        }
        return null;
    }

    public List<Order> getOrdersByCustomer(String customerId) {
        List<Order> customerOrders = new ArrayList<>();
        for (Order o : orders) {
            if (o.getCustomerId().equals(customerId)) {
                customerOrders.add(o);
            }
        }
        return customerOrders;
    }

    public void updateOrderStatus(String orderId, OrderStatus newStatus) throws InvalidOrderException {
        Order order = findOrderById(orderId);
        if (order != null) {
            order.updateStatus(newStatus);
            saveToFile();
        }
    }

    public List<Order> getAllOrders() {
        return new ArrayList<>(orders);
    }
}
