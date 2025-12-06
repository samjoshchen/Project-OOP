package com.martminds.model.product;

public class Product {
    private String productId;
    private String name;
    private double price;
    private int stock;
    private String description;
    private String storeId;
    private String category;

    public Product(String productId, String name, double price, int stock, String description, String storeId, String category) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.description = description;
        this.storeId = storeId;
        this.category = category;
    }

    public String getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public String getDescription() {
        return description;
    }

    public String getStoreId() {
        return storeId;
    }

    public String getCategory() {
        return category;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void updateStock(int quantity) {
        this.stock += quantity;
    }

    public boolean isAvailable() {
        return stock > 0;
    }

    @Override
    public String toString() {
        return String.format("Product[ID=%s, Name=%s, Price=%.2f, Stock=%d]",
            productId, name, price, stock);
    }
}
