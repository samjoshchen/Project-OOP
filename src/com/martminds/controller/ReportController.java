package com.martminds.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import com.martminds.enums.OrderStatus;
import com.martminds.enums.PaymentStatus;
import com.martminds.enums.UserRole;
import com.martminds.model.order.Order;
import com.martminds.model.payment.Payment;
import com.martminds.model.product.Product;
import com.martminds.model.user.Driver;
import com.martminds.model.user.User;
import com.martminds.service.*;
import com.martminds.util.Session;

public class ReportController {
        private OrderService orderService;
        private PaymentService paymentService;
        private ProductService productService;
        private UserService userService;
        private DriverService driverService;

        public ReportController() {
                this.orderService = OrderService.getInstance();
                this.paymentService = PaymentService.getInstance();
                this.productService = ProductService.getInstance();
                this.userService = UserService.getInstance();
                this.driverService = DriverService.getInstance();
        }

        public String generateSalesReport() {
                Session.getInstance().requireAdmin();

                StringBuilder report = new StringBuilder();
                report.append("\n╔════════════════════════════════════════════════════════════╗\n");
                report.append("║                     SALES REPORT                           ║\n");
                report.append("╚════════════════════════════════════════════════════════════╝\n\n");

                List<Order> allOrders = orderService.getAllOrders();
                List<Order> completedOrders = allOrders.stream()
                                .filter(order -> order.getStatus() == OrderStatus.DELIVERED)
                                .collect(Collectors.toList());

                double totalRevenue = completedOrders.stream()
                                .mapToDouble(Order::getTotalPrice)
                                .sum();

                Map<OrderStatus, Long> ordersByStatus = allOrders.stream()
                                .collect(Collectors.groupingBy(Order::getStatus, Collectors.counting()));

                report.append(String.format("Generated: %s\n\n",
                                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));

                report.append("SUMMARY\n");
                report.append("  ────────────────────────────────────────────────────────────\n");
                report.append(String.format("  Total Orders:          %d\n", allOrders.size()));
                report.append(String.format("  Completed Orders:      %d\n", completedOrders.size()));
                report.append(String.format("  Total Revenue:         Rp %,.0f\n", totalRevenue));
                report.append(String.format("  Average Order Value:   Rp %,.0f\n\n",
                                completedOrders.isEmpty() ? 0 : totalRevenue / completedOrders.size()));

                report.append("ORDERS BY STATUS\n");
                report.append("  ────────────────────────────────────────────────────────────\n");
                for (OrderStatus status : OrderStatus.values()) {
                        long count = ordersByStatus.getOrDefault(status, 0L);
                        report.append(String.format("  %-25s: %d\n", status.toString(), count));
                }
                report.append("\n");

                Map<String, Integer> productSales = new HashMap<>();
                for (Order order : completedOrders) {
                        order.getItems().forEach(item -> {
                                productSales.merge(item.getProductId(), item.getQuantity(), Integer::sum);
                        });
                }

                if (!productSales.isEmpty()) {
                        report.append("TOP SELLING PRODUCTS\n");
                        report.append("  ────────────────────────────────────────────────────────────\n");
                        productSales.entrySet().stream()
                                        .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                                        .limit(5)
                                        .forEach(entry -> {
                                                Product product = productService.getProductById(entry.getKey());
                                                String productName = product != null ? product.getName()
                                                                : entry.getKey();
                                                report.append(String.format("  %-30s: %d units sold\n",
                                                                productName.substring(0,
                                                                                Math.min(30, productName.length())),
                                                                entry.getValue()));
                                        });
                        report.append("\n");
                }

                return report.toString();
        }

        public String generatePaymentReport() {
                Session.getInstance().requireAdmin();

                StringBuilder report = new StringBuilder();
                report.append("\n╔════════════════════════════════════════════════════════════╗\n");
                report.append("║                   PAYMENT REPORT                           ║\n");
                report.append("╚════════════════════════════════════════════════════════════╝\n\n");

                List<Payment> allPayments = paymentService.getAllPayments();
                List<Payment> successfulPayments = allPayments.stream()
                                .filter(payment -> payment.getStatus() == PaymentStatus.SUCCESS)
                                .collect(Collectors.toList());

                double totalAmount = successfulPayments.stream()
                                .mapToDouble(Payment::getAmount)
                                .sum();

                report.append(String.format("Generated: %s\n\n",
                                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));

                report.append("SUMMARY\n");
                report.append("  ────────────────────────────────────────────────────────────\n");
                report.append(String.format("  Total Payments:        %d\n", allPayments.size()));
                report.append(String.format("  Successful Payments:   %d\n", successfulPayments.size()));
                report.append(String.format("  Total Amount:          Rp %,.0f\n\n", totalAmount));

                Map<PaymentStatus, Long> paymentsByStatus = allPayments.stream()
                                .collect(Collectors.groupingBy(Payment::getStatus, Collectors.counting()));

                report.append("PAYMENTS BY STATUS\n");
                report.append("  ────────────────────────────────────────────────────────────\n");
                for (PaymentStatus status : PaymentStatus.values()) {
                        long count = paymentsByStatus.getOrDefault(status, 0L);
                        report.append(String.format("  %-20s: %d\n", status.toString(), count));
                }
                report.append("\n");

                Map<String, Long> paymentsByMethod = successfulPayments.stream()
                                .collect(Collectors.groupingBy(payment -> payment.getClass().getSimpleName(),
                                                Collectors.counting()));

                Map<String, Double> amountByMethod = new HashMap<>();
                for (Payment payment : successfulPayments) {
                        String method = payment.getClass().getSimpleName();
                        amountByMethod.merge(method, payment.getAmount(), Double::sum);
                }

                report.append("PAYMENTS BY METHOD\n");
                report.append("  ────────────────────────────────────────────────────────────\n");
                for (Map.Entry<String, Long> entry : paymentsByMethod.entrySet()) {
                        String method = entry.getKey().replace("Payment", "");
                        double amount = amountByMethod.get(entry.getKey());
                        report.append(String.format("  %-20s: %d payments (Rp %,.0f)\n",
                                        method, entry.getValue(), amount));
                }
                report.append("\n");

                return report.toString();
        }

        public String generateInventoryReport() {
                Session.getInstance().requireAdmin();

                StringBuilder report = new StringBuilder();
                report.append("\n╔════════════════════════════════════════════════════════════╗\n");
                report.append("║                  INVENTORY REPORT                          ║\n");
                report.append("╚════════════════════════════════════════════════════════════╝\n\n");

                List<Product> allProducts = productService.getAllProducts();

                report.append(String.format("Generated: %s\n\n",
                                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));

                report.append("SUMMARY\n");
                report.append("  ────────────────────────────────────────────────────────────\n");
                report.append(String.format("  Total Products:        %d\n", allProducts.size()));

                long inStock = allProducts.stream().filter(p -> p.getStock() > 0).count();
                long outOfStock = allProducts.stream().filter(p -> p.getStock() == 0).count();
                long lowStock = allProducts.stream().filter(p -> p.getStock() > 0 && p.getStock() < 10).count();

                report.append(String.format("  In Stock:              %d\n", inStock));
                report.append(String.format("  Out of Stock:          %d\n", outOfStock));
                report.append(String.format("  Low Stock (< 10):      %d\n\n", lowStock));

                double totalValue = allProducts.stream()
                                .mapToDouble(p -> p.getPrice() * p.getStock())
                                .sum();
                report.append(String.format("  Total Inventory Value: Rp %,.0f\n\n", totalValue));

                Map<String, Long> productsByCategory = allProducts.stream()
                                .collect(Collectors.groupingBy(Product::getCategory, Collectors.counting()));

                report.append("PRODUCTS BY CATEGORY\n");
                report.append("  ────────────────────────────────────────────────────────────\n");
                for (Map.Entry<String, Long> entry : productsByCategory.entrySet()) {
                        report.append(String.format("  %-30s: %d products\n", entry.getKey(), entry.getValue()));
                }
                report.append("\n");

                if (lowStock > 0) {
                        report.append("LOW STOCK ALERT\n");
                        report.append("  ────────────────────────────────────────────────────────────\n");
                        allProducts.stream()
                                        .filter(p -> p.getStock() > 0 && p.getStock() < 10)
                                        .sorted(Comparator.comparingInt(Product::getStock))
                                        .forEach(p -> report.append(String.format("  %-10s %-30s: %d units\n",
                                                        p.getProductId(),
                                                        p.getName().substring(0, Math.min(30, p.getName().length())),
                                                        p.getStock())));
                        report.append("\n");
                }

                if (outOfStock > 0) {
                        report.append("OUT OF STOCK\n");
                        report.append("  ────────────────────────────────────────────────────────────\n");
                        allProducts.stream()
                                        .filter(p -> p.getStock() == 0)
                                        .forEach(p -> report.append(String.format("  %-10s %s\n",
                                                        p.getProductId(),
                                                        p.getName().substring(0, Math.min(48, p.getName().length())))));
                        report.append("\n");
                }

                return report.toString();
        }

        public String generateUserStatistics() {
                Session.getInstance().requireAdmin();

                StringBuilder report = new StringBuilder();
                report.append("\n╔════════════════════════════════════════════════════════════╗\n");
                report.append("║                  USER STATISTICS                           ║\n");
                report.append("╚════════════════════════════════════════════════════════════╝\n\n");

                List<User> allUsers = userService.getAllUsers();

                report.append(String.format("Generated: %s\n\n",
                                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));

                report.append("SUMMARY\n");
                report.append("  ────────────────────────────────────────────────────────────\n");
                report.append(String.format("  Total Users:           %d\n\n", allUsers.size()));

                Map<UserRole, Long> usersByRole = allUsers.stream()
                                .collect(Collectors.groupingBy(User::getRole, Collectors.counting()));

                report.append("USERS BY ROLE\n");
                report.append("  ────────────────────────────────────────────────────────────\n");
                for (UserRole role : UserRole.values()) {
                        long count = usersByRole.getOrDefault(role, 0L);
                        report.append(String.format("  %-20s: %d\n", role.toString(), count));
                }
                report.append("\n");

                Map<String, Object> driverStats = driverService.getAllDriversStatistics();
                report.append("DRIVER STATISTICS\n");
                report.append("  ────────────────────────────────────────────────────────────\n");
                report.append(String.format("  Total Drivers:         %d\n", driverStats.get("total_drivers")));
                report.append(String.format("  Available Drivers:     %d\n", driverStats.get("available_drivers")));
                report.append(String.format("  Total Deliveries:      %d\n\n", driverStats.get("total_deliveries")));

                List<Driver> drivers = driverService.getAllDrivers();
                if (!drivers.isEmpty()) {
                        report.append("TOP PERFORMING DRIVERS\n");
                        report.append("  ────────────────────────────────────────────────────────────\n");
                        drivers.stream()
                                        .sorted((d1, d2) -> {
                                                int deliveries1 = driverService.getDriverStatistics(d1.getUserId())
                                                                .get("completed");
                                                int deliveries2 = driverService.getDriverStatistics(d2.getUserId())
                                                                .get("completed");
                                                return Integer.compare(deliveries2, deliveries1);
                                        })
                                        .limit(5)
                                        .forEach(driver -> {
                                                Map<String, Integer> stats = driverService
                                                                .getDriverStatistics(driver.getUserId());
                                                report.append(String.format("  %-15s %-25s: %d deliveries\n",
                                                                driver.getUserId(),
                                                                driver.getName().substring(0,
                                                                                Math.min(25, driver.getName()
                                                                                                .length())),
                                                                stats.get("completed")));
                                        });
                        report.append("\n");
                }

                List<Order> allOrders = orderService.getAllOrders();
                long customersWithOrders = allOrders.stream()
                                .map(Order::getCustomerId)
                                .distinct()
                                .count();

                report.append("CUSTOMER STATISTICS\n");
                report.append("  ────────────────────────────────────────────────────────────\n");
                report.append(String.format("  Customers with Orders: %d\n", customersWithOrders));

                double avgOrdersPerCustomer = customersWithOrders > 0 ? (double) allOrders.size() / customersWithOrders
                                : 0.0;
                report.append(String.format("  Avg Orders/Customer:   %.1f\n\n", avgOrdersPerCustomer));

                return report.toString();
        }

        public String generateDashboard() {
                Session.getInstance().requireAdmin();

                StringBuilder dashboard = new StringBuilder();
                dashboard.append("\n╔════════════════════════════════════════════════════════════╗\n");
                dashboard.append("║                   ADMIN DASHBOARD                          ║\n");
                dashboard.append("╚════════════════════════════════════════════════════════════╝\n\n");

                dashboard.append(String.format("Generated: %s\n\n",
                                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));

                List<Order> orders = orderService.getAllOrders();
                List<Product> products = productService.getAllProducts();
                List<User> users = userService.getAllUsers();
                List<Payment> payments = paymentService.getAllPayments();

                double totalRevenue = orders.stream()
                                .filter(o -> o.getStatus() == OrderStatus.DELIVERED)
                                .mapToDouble(Order::getTotalPrice)
                                .sum();

                dashboard.append("QUICK OVERVIEW\n");
                dashboard.append("  ────────────────────────────────────────────────────────────\n");
                dashboard.append(String.format("  Total Users:           %d\n", users.size()));
                dashboard.append(String.format("  Total Products:        %d\n", products.size()));
                dashboard.append(String.format("  Total Orders:          %d\n", orders.size()));
                dashboard.append(String.format("  Total Payments:        %d\n", payments.size()));
                dashboard.append(String.format("  Total Revenue:         Rp %,.0f\n\n", totalRevenue));

                long pendingOrders = orders.stream()
                                .filter(o -> o.getStatus() == OrderStatus.PENDING)
                                .count();
                long outForDelivery = orders.stream()
                                .filter(o -> o.getStatus() == OrderStatus.OUT_FOR_DELIVERY)
                                .count();
                long lowStockProducts = products.stream()
                                .filter(p -> p.getStock() > 0 && p.getStock() < 10)
                                .count();

                dashboard.append("ALERTS & NOTIFICATIONS\n");
                dashboard.append("  ────────────────────────────────────────────────────────────\n");
                dashboard.append(String.format("  Pending Orders:        %d\n", pendingOrders));
                dashboard.append(String.format("  Out for Delivery:      %d\n", outForDelivery));
                dashboard.append(String.format("  Low Stock Products:    %d\n\n", lowStockProducts));

                return dashboard.toString();
        }
}
