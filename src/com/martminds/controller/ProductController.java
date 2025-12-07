package com.martminds.controller;

import java.util.List;

import com.martminds.model.product.Product;
import com.martminds.service.ProductService;
import com.martminds.util.Session;
import com.martminds.util.ValidationUtil;
import com.martminds.enums.UserRole;

public class ProductController {

    public Product createProduct(String productId, String name, double price, int stock,
            String description, String storeId, String category) {
        Session.getInstance().requireRole(UserRole.ADMIN);

        if (!ValidationUtil.isNotEmpty(productId)) {
            throw new IllegalArgumentException("Product ID cannot be empty");
        }
        if (!ValidationUtil.isNotEmpty(name)) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }
        if (!ValidationUtil.isValidPrice(price)) {
            throw new IllegalArgumentException("Price must be greater than 0");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }
        if (!ValidationUtil.isNotEmpty(storeId)) {
            throw new IllegalArgumentException("Store ID cannot be empty");
        }
        if (!ValidationUtil.isNotEmpty(category)) {
            throw new IllegalArgumentException("Category cannot be empty");
        }

        if (ProductService.getInstance().getProductById(productId) != null) {
            throw new IllegalArgumentException("Product with ID " + productId + " already exists");
        }

        Product product = new Product(productId, name, price, stock, description, storeId, category);
        ProductService.getInstance().addProduct(product);
        return product;
    }

    public Product getProductById(String productId) {
        if (!ValidationUtil.isNotEmpty(productId)) {
            throw new IllegalArgumentException("Product ID cannot be empty");
        }

        Product product = ProductService.getInstance().getProductById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Product not found: " + productId);
        }
        return product;
    }

    public List<Product> getAllProducts() {
        return ProductService.getInstance().getAllProducts();
    }

    public List<Product> getProductsByCategory(String category) {
        if (!ValidationUtil.isNotEmpty(category)) {
            throw new IllegalArgumentException("Category cannot be empty");
        }
        return ProductService.getInstance().getProductsByCategory(category);
    }

    public List<Product> getProductsByStore(String storeId) {
        if (!ValidationUtil.isNotEmpty(storeId)) {
            throw new IllegalArgumentException("Store ID cannot be empty");
        }
        return ProductService.getInstance().getProductsByStore(storeId);
    }

    public List<Product> getAvailableProducts() {
        return ProductService.getInstance().getAvailableProducts();
    }

    public boolean updateProductStock(String productId, int quantity) {
        Session.getInstance().requireRole(UserRole.ADMIN);

        if (!ValidationUtil.isNotEmpty(productId)) {
            throw new IllegalArgumentException("Product ID cannot be empty");
        }

        Product product = ProductService.getInstance().getProductById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Product not found: " + productId);
        }

        return ProductService.getInstance().updateStock(productId, quantity);
    }

    public boolean updateProductPrice(String productId, double newPrice) {
        Session.getInstance().requireRole(UserRole.ADMIN);

        if (!ValidationUtil.isNotEmpty(productId)) {
            throw new IllegalArgumentException("Product ID cannot be empty");
        }
        if (!ValidationUtil.isValidPrice(newPrice)) {
            throw new IllegalArgumentException("Price must be greater than 0");
        }

        Product product = ProductService.getInstance().getProductById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Product not found: " + productId);
        }

        product.setPrice(newPrice);
        return true;
    }

    public boolean updateProduct(String productId, String name, double price,
            String description, String category) {
        Session.getInstance().requireRole(UserRole.ADMIN);

        if (!ValidationUtil.isNotEmpty(productId)) {
            throw new IllegalArgumentException("Product ID cannot be empty");
        }

        Product product = ProductService.getInstance().getProductById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Product not found: " + productId);
        }

        if (ValidationUtil.isNotEmpty(name)) {
            product.setName(name);
        }
        if (price > 0) {
            product.setPrice(price);
        }
        if (ValidationUtil.isNotEmpty(description)) {
            product.setDescription(description);
        }
        if (ValidationUtil.isNotEmpty(category)) {
            product.setCategory(category);
        }

        return true;
    }

    public boolean deleteProduct(String productId) {
        Session.getInstance().requireRole(UserRole.ADMIN);

        if (!ValidationUtil.isNotEmpty(productId)) {
            throw new IllegalArgumentException("Product ID cannot be empty");
        }

        Product product = ProductService.getInstance().getProductById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Product not found: " + productId);
        }

        return ProductService.getInstance().removeProduct(productId);
    }

    public List<String> getAllCategories() {
        return ProductService.getInstance().getAllCategories();
    }

    public boolean restockProduct(String productId, int additionalStock) {
        Session.getInstance().requireRole(UserRole.ADMIN);

        if (!ValidationUtil.isNotEmpty(productId)) {
            throw new IllegalArgumentException("Product ID cannot be empty");
        }
        if (additionalStock <= 0) {
            throw new IllegalArgumentException("Additional stock must be greater than 0");
        }

        Product product = ProductService.getInstance().getProductById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Product not found: " + productId);
        }

        int newStock = product.getStock() + additionalStock;
        return ProductService.getInstance().updateStock(productId, newStock);
    }
}
