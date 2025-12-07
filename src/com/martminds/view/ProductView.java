package com.martminds.view;

import com.martminds.model.product.Product;
import com.martminds.util.Input;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class ProductView {

    public void displayProduct(Product product) {
        System.out.println("\nProduct Details\n");
        System.out.println("ID          : " + product.getProductId());
        System.out.println("Name        : " + product.getName());
        System.out.println("Price       : " + product.getPrice());
        System.out.println("Stock       : " + product.getStock());
        System.out.println("Category    : " + product.getCategory());
        System.out.println("Description : " + product.getDescription());
        System.out.println("Store ID    : " + product.getStoreId());
        System.out.println();
    }

    public void displayProductList(List<Product> products) {
        if (products == null || products.isEmpty()) {
            System.out.println("\nNo products available.");
            return;
        }
        
        System.out.println("\nProduct List\n");
        System.out.printf("%-10s %-25s %-12s %-8s %-15s\n", 
            "ID", "Name", "Price", "Stock", "Category");
        System.out.println("\n");
        
        for (Product p : products) {
            System.out.printf("%-10s %-25s Rp%-10.0f %-8d %-15s\n",
                p.getProductId(),
                p.getName().length() > 25 ? p.getName().substring(0, 22) + "..." : p.getName(),
                p.getPrice(),
                p.getStock(),
                p.getCategory());
        }
        System.out.println("\n");
    }
    
    public void displayProductDetails(Product product) {
        displayProduct(product);
    }
    
    public Map<String, String> getProductInput() {
        Map<String, String> data = new HashMap<>();
        
        System.out.println("\nAdd New Product\n");
        data.put("id", Input.promptString("Product ID (e.g., P001): "));
        data.put("name", Input.promptString("Product Name: "));
        data.put("price", Input.promptString("Price: Rp "));
        data.put("stock", String.valueOf(Input.promptInt("Stock: ")));
        data.put("description", Input.promptString("Description: "));
        data.put("category", Input.promptString("Category: "));
        
        return data;
    }
    
    public String getProductIdInput() {
        return Input.promptString("Enter Product ID: ");
    }
    
    public Map<String, String> getProductUpdateInput() {
        Map<String, String> data = new HashMap<>();
        
        System.out.println("\nUpdate Product (Leave blank to skip)\n");
        data.put("name", Input.promptString("New Name: "));
        data.put("price", Input.promptString("New Price: "));
        data.put("description", Input.promptString("New Description: "));
        data.put("category", Input.promptString("New Category: "));
        
        return data;
    }
}
