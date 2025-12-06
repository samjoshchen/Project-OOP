package com.martminds.service;

import com.martminds.model.product.Product;
import java.util.ArrayList;
import java.util.List;

public class ProductService {
    private static ProductService instance;
    private List<Product> products;
    
    private ProductService() {
        this.products = new ArrayList<>();
        createSampleProducts();
    }
    
    public static ProductService getInstance() {
        if (instance == null) {
            instance = new ProductService();
        }
        return instance;
    }
    
    public List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }
    
    public Product getProductById(String productId) {
        return products.stream()
            .filter(p -> p.getProductId().equals(productId))
            .findFirst()
            .orElse(null);
    }
    
    public boolean addProduct(Product product) {
        if (product == null || getProductById(product.getProductId()) != null) {
            return false;
        }
        
        products.add(product);
        return true;
    }

    public boolean updateStock(String productId, int quantity) {
        Product product = getProductById(productId);
        
        if (product == null) {
            return false;
        }
        
        int newStock = product.getStock() + quantity;
        
        if (newStock < 0) {
            return false;
        }
        
        product.updateStock(quantity);
        return true;
    }
    
    private void createSampleProducts() {
        products.add(new Product("P001", "Indomie Goreng", 3500, 100, "Mie instan rasa goreng original", "S001", "Food"));
        products.add(new Product("P002", "Aqua 600ml", 4000, 150, "Air mineral kemasan botol", "S001", "Beverage"));
        products.add(new Product("P003", "Silverqueen Chocolate", 12000, 50, "Coklat susu premium", "S001", "Snack"));
        products.add(new Product("P004", "Teh Botol Sosro", 5000, 80, "Minuman teh dalam botol", "S001", "Beverage"));
        products.add(new Product("P005", "Chitato Sapi Panggang", 10000, 60, "Keripik kentang rasa sapi panggang", "S001", "Snack"));
        products.add(new Product("P006", "Ultra Milk Coklat", 7000, 70, "Susu UHT rasa coklat 250ml", "S001", "Beverage"));
        products.add(new Product("P007", "Sarimi Ayam Bawang", 3000, 120, "Mie instan kuah rasa ayam bawang", "S001", "Food"));
        products.add(new Product("P008", "Good Day Cappuccino", 2500, 90, "Kopi instan rasa cappuccino", "S001", "Beverage"));
        products.add(new Product("P009", "Oreo Original", 9500, 45, "Biskuit sandwich krim vanilla", "S001", "Snack"));
        products.add(new Product("P010", "Pocari Sweat 500ml", 8000, 65, "Minuman isotonik pengganti ion tubuh", "S001", "Beverage"));
    }
}
