package com.martminds.controller;

import java.util.*;
import java.util.stream.Collectors;

import com.martminds.model.common.Address;
import com.martminds.model.order.Order;
import com.martminds.model.product.Product;
import com.martminds.model.store.Store;
import com.martminds.service.OrderService;
import com.martminds.service.ProductService;
import com.martminds.util.Session;
import com.martminds.util.ValidationUtil;

public class StoreController {
    private List<Store> stores;
    private ProductService productService;
    private OrderService orderService;

    public StoreController() {
        this.stores = new ArrayList<>();
        this.productService = ProductService.getInstance();
        this.orderService = OrderService.getInstance();
        initializeDefaultStores();
    }

    private void initializeDefaultStores() {
        if (stores.isEmpty()) {

            Address defaultAddress = new Address(
                    "123 Main Street",
                    "Jakarta",
                    "12170",
                    "Central Jakarta",
                    "DKI Jakarta");

            Store defaultStore = new Store(
                    "S001",
                    "MartMinds Central",
                    defaultAddress,
                    "081234567890");

            stores.add(defaultStore);
        }
    }

    public List<Store> getAllStores() {
        return new ArrayList<>(stores);
    }

    public Store getStoreById(String storeId) {
        if (!ValidationUtil.isNotEmpty(storeId)) {
            return null;
        }

        return stores.stream()
                .filter(store -> store.getStoreId().equalsIgnoreCase(storeId))
                .findFirst()
                .orElse(null);
    }

    public Store createStore(String storeId, String name, Address address, String contactNumber) {
        Session.getInstance().requireAdmin();

        if (!ValidationUtil.isNotEmpty(storeId) || !ValidationUtil.isNotEmpty(name)) {
            throw new IllegalArgumentException("Store ID and name are required");
        }

        if (getStoreById(storeId) != null) {
            throw new IllegalArgumentException("Store with ID " + storeId + " already exists");
        }

        if (address == null) {
            throw new IllegalArgumentException("Store address is required");
        }

        Store newStore = new Store(storeId, name, address, contactNumber);
        stores.add(newStore);

        return newStore;
    }

    public boolean updateStore(String storeId, String name, Address address, String contactNumber) {
        Session.getInstance().requireAdmin();

        Store store = getStoreById(storeId);
        if (store == null) {
            return false;
        }

        if (ValidationUtil.isNotEmpty(name)) {
            store.setName(name);
        }

        if (address != null) {
            store.setAddress(address);
        }

        if (ValidationUtil.isNotEmpty(contactNumber)) {
            store.setContactNumber(contactNumber);
        }

        return true;
    }

    public boolean deleteStore(String storeId) {
        Session.getInstance().requireAdmin();

        Store store = getStoreById(storeId);
        if (store == null) {
            return false;
        }

        long productsInStore = productService.getAllProducts().stream()
                .filter(p -> storeId.equals(p.getStoreId()))
                .count();

        if (productsInStore > 0) {
            throw new IllegalStateException("Cannot delete store with active products. Remove products first.");
        }

        return stores.remove(store);
    }

    public List<Product> getStoreProducts(String storeId) {
        if (!ValidationUtil.isNotEmpty(storeId)) {
            return new ArrayList<>();
        }

        return productService.getAllProducts().stream()
                .filter(product -> storeId.equals(product.getStoreId()))
                .collect(Collectors.toList());
    }

    public List<Order> getStoreOrders(String storeId) {
        if (!ValidationUtil.isNotEmpty(storeId)) {
            return new ArrayList<>();
        }

        return orderService.getAllOrders().stream()
                .filter(order -> storeId.equals(order.getStoreId()))
                .collect(Collectors.toList());
    }

    public List<Store> searchStoresByName(String searchTerm) {
        if (!ValidationUtil.isNotEmpty(searchTerm)) {
            return getAllStores();
        }

        String lowerSearchTerm = searchTerm.toLowerCase();
        return stores.stream()
                .filter(store -> store.getName().toLowerCase().contains(lowerSearchTerm))
                .collect(Collectors.toList());
    }

    public List<Store> searchStoresByCity(String city) {
        if (!ValidationUtil.isNotEmpty(city)) {
            return getAllStores();
        }

        String lowerCity = city.toLowerCase();
        return stores.stream()
                .filter(store -> store.getAddress().getCity().toLowerCase().contains(lowerCity))
                .collect(Collectors.toList());
    }

    public Map<String, Object> getStoreStatistics(String storeId) {
        Map<String, Object> stats = new HashMap<>();

        Store store = getStoreById(storeId);
        if (store == null) {
            stats.put("store_found", false);
            return stats;
        }

        stats.put("store_found", true);
        stats.put("store_name", store.getName());
        stats.put("store_rating", store.getRating());
        stats.put("total_ratings", store.getTotalRate());

        List<Product> storeProducts = getStoreProducts(storeId);
        stats.put("total_products", storeProducts.size());

        long availableProducts = storeProducts.stream()
                .filter(p -> p.getStock() > 0)
                .count();
        stats.put("available_products", availableProducts);

        List<Order> storeOrders = getStoreOrders(storeId);
        stats.put("total_orders", storeOrders.size());

        return stats;
    }

    public boolean addStoreRating(String storeId, double rating) {
        if (rating < 0.0 || rating > 5.0) {
            throw new IllegalArgumentException("Rating must be between 0.0 and 5.0");
        }

        Store store = getStoreById(storeId);
        if (store == null) {
            return false;
        }

        store.addRating(rating);
        return true;
    }

    public String getStorePerformanceSummary(String storeId) {
        Map<String, Object> stats = getStoreStatistics(storeId);

        if (!(boolean) stats.get("store_found")) {
            return "Store not found";
        }

        StringBuilder summary = new StringBuilder();
        summary.append("\n╔════════════════════════════════════════════════════════════╗\n");
        summary.append("║               STORE PERFORMANCE SUMMARY                    ║\n");
        summary.append("╚════════════════════════════════════════════════════════════╝\n\n");

        summary.append(String.format("  Store Name:            %s\n", stats.get("store_name")));
        summary.append(String.format("  Store ID:              %s\n", storeId));
        summary.append(String.format("  Rating:                %.1f/5.0 (%d ratings)\n",
                stats.get("store_rating"), stats.get("total_ratings")));
        summary.append(String.format("  Total Products:        %d\n", stats.get("total_products")));
        summary.append(String.format("  Available Products:    %d\n", stats.get("available_products")));
        summary.append(String.format("  Total Orders:          %d\n\n", stats.get("total_orders")));

        return summary.toString();
    }

    public String getAllStoresSummary() {
        Session.getInstance().requireAdmin();

        StringBuilder summary = new StringBuilder();
        summary.append("\n╔════════════════════════════════════════════════════════════╗\n");
        summary.append("║                   ALL STORES SUMMARY                       ║\n");
        summary.append("╚════════════════════════════════════════════════════════════╝\n\n");

        summary.append(String.format("  Total Stores: %d\n\n", stores.size()));

        summary.append(String.format("  %-10s %-25s %-20s %-10s\n",
                "Store ID", "Name", "City", "Rating"));
        summary.append("  ────────────────────────────────────────────────────────────\n");

        for (Store store : stores) {
            summary.append(String.format("  %-10s %-25s %-20s %.1f/5.0\n",
                    store.getStoreId(),
                    store.getName().substring(0, Math.min(25, store.getName().length())),
                    store.getAddress().getCity().substring(0, Math.min(20, store.getAddress().getCity().length())),
                    store.getRating()));
        }
        summary.append("\n");

        return summary.toString();
    }
}
