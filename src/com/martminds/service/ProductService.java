package com.martminds.service;

import com.martminds.model.product.Product;
import com.martminds.util.FileHandler;
import com.martminds.util.Logger;
import java.util.ArrayList;
import java.util.List;

public class ProductService {
    private static ProductService instance;
    private List<Product> products;
    private static final String PRODUCT_FILE = "products.csv";

    private ProductService() {
        this.products = new ArrayList<>();
        loadFromFile();
        if (products.isEmpty()) {
            createSampleProducts();
            saveToFile();
        }
    }

    public static ProductService getInstance() {
        if (instance == null) {
            instance = new ProductService();
        }
        return instance;
    }

    private void loadFromFile() {
        List<String> lines = FileHandler.readFile(PRODUCT_FILE);

        for (String line : lines) {
            if (line.trim().isEmpty())
                continue;

            try {
                String[] fields = FileHandler.parseCSVLine(line);
                if (fields.length < 7)
                    continue;

                String productId = fields[0];
                String name = fields[1];
                double price = Double.parseDouble(fields[2]);
                int stock = Integer.parseInt(fields[3]);
                String description = fields[4];
                String storeId = fields[5];
                String category = fields[6];

                Product product = new Product(productId, name, price, stock, description, storeId, category);
                products.add(product);
            } catch (Exception e) {
                Logger.error("Error parsing product line: " + line + " - " + e.getMessage());
            }
        }

        Logger.info("Loaded " + products.size() + " products from file");
    }

    private void saveToFile() {
        List<String> lines = new ArrayList<>();

        for (Product product : products) {
            String line = FileHandler.formatCSVLine(
                    product.getProductId(),
                    product.getName(),
                    String.valueOf(product.getPrice()),
                    String.valueOf(product.getStock()),
                    product.getDescription(),
                    product.getStoreId(),
                    product.getCategory());
            lines.add(line);
        }

        FileHandler.writeFile(PRODUCT_FILE, lines);
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

    public List<Product> getProductsByCategory(String category) {
        List<Product> results = new ArrayList<>();

        for (Product product : products) {
            if (product.getCategory().equalsIgnoreCase(category)) {
                results.add(product);
            }
        }

        return results;
    }

    public boolean addProduct(Product product) {
        if (product == null || getProductById(product.getProductId()) != null) {
            return false;
        }

        products.add(product);
        saveToFile();
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
        saveToFile();
        return true;
    }

    public List<Product> getProductsByStore(String storeId) {
        List<Product> results = new ArrayList<>();
        for (Product product : products) {
            if (product.getStoreId().equals(storeId)) {
                results.add(product);
            }
        }
        return results;
    }

    public List<Product> getAvailableProducts() {
        List<Product> results = new ArrayList<>();
        for (Product product : products) {
            if (product.isAvailable()) {
                results.add(product);
            }
        }
        return results;
    }

    public boolean removeProduct(String productId) {
        Product product = getProductById(productId);
        if (product == null) {
            return false;
        }
        boolean removed = products.remove(product);
        if (removed) {
            saveToFile();
        }
        return removed;
    }

    public List<String> getAllCategories() {
        List<String> categories = new ArrayList<>();
        for (Product product : products) {
            if (!categories.contains(product.getCategory())) {
                categories.add(product.getCategory());
            }
        }
        return categories;
    }

    public boolean validateProductAvailability(String productId, int requiredQuantity) {
        Product product = getProductById(productId);
        return product != null && product.getStock() >= requiredQuantity;
    }

    private void createSampleProducts() {
        products.add(
                new Product("P001", "Indomie Goreng", 3500, 100, "Mie instan rasa goreng original", "S001", "Food"));
        products.add(new Product("P002", "Aqua 600ml", 4000, 150, "Air mineral kemasan botol", "S001", "Beverage"));
        products.add(new Product("P003", "Silverqueen Chocolate", 12000, 50, "Coklat susu premium", "S001", "Snack"));
        products.add(new Product("P004", "Teh Botol Sosro", 5000, 80, "Minuman teh dalam botol", "S001", "Beverage"));
        products.add(new Product("P005", "Chitato Sapi Panggang", 10000, 60, "Keripik kentang rasa sapi panggang",
                "S001", "Snack"));
        products.add(
                new Product("P006", "Ultra Milk Coklat", 7000, 70, "Susu UHT rasa coklat 250ml", "S001", "Beverage"));
        products.add(new Product("P007", "Sarimi Ayam Bawang", 3000, 120, "Mie instan kuah rasa ayam bawang", "S001",
                "Food"));
        products.add(new Product("P008", "Good Day Cappuccino", 2500, 90, "Kopi instan rasa cappuccino", "S001",
                "Beverage"));
        products.add(new Product("P009", "Oreo Original", 9500, 45, "Biskuit sandwich krim vanilla", "S001", "Snack"));
        products.add(new Product("P010", "Pocari Sweat 500ml", 8000, 65, "Minuman isotonik pengganti ion tubuh", "S001",
                "Beverage"));
    }
}
