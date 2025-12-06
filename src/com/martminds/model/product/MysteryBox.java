package com.martminds.model.product;

import java.util.ArrayList;
import java.util.List;

public class MysteryBox {
    private String boxId;
    private String name;
    private double price;
    private String category;
    private String description;
    private int availableStock;
    private String storeId;
    private List<Product> possibleProducts;

    public MysteryBox(String boxId, String name, double price, String category, String description, int availableStock,
            String storeId) {
        this.boxId = boxId;
        this.name = name;
        this.price = price;
        this.category = category;
        this.description = description;
        this.availableStock = availableStock;
        this.storeId = storeId;
        this.possibleProducts = new ArrayList<>();
    }

    public String getBoxId() {
        return boxId;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public int getAvailableStock() {
        return availableStock;
    }

    public String getStoreId() {
        return storeId;
    }

    public List<Product> getPossibleProducts() {
        return new ArrayList<>(possibleProducts);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAvailableStock(int availableStock) {
        this.availableStock = availableStock;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public void addPossibleProduct(Product product) {
        if (product != null && !possibleProducts.contains(product)) {
            possibleProducts.add(product);
        }
    }

    public void removePossibleProduct(Product product) {
        possibleProducts.remove(product);
    }

    public Product getPossibleProduct(int index) {
        if (index >= 0 && index < possibleProducts.size()) {
            return possibleProducts.get(index);
        }
        return null;
    }

    public boolean isAvailable() {
        return availableStock > 0 && !possibleProducts.isEmpty();
    }

    public void updateStock(int quantity) {
        this.availableStock += quantity;
    }

    @Override
    public String toString() {
        return String.format("MysteryBox[ID=%s, Name=%s, Price=%.2f, Stock=%d, Products=%d]",
                boxId, name, price, availableStock, possibleProducts.size());
    }
}
