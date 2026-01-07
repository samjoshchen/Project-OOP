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
        System.out.println("----------+-------------------------+------------+--------+---------------");

        for (Product p : products) {
            System.out.printf("%-10s %-25s Rp%-10.0f %-8d %-15s\n",
                    p.getProductId(),
                    p.getName().length() > 25 ? p.getName().substring(0, 22) + "..." : p.getName(),
                    p.getPrice(),
                    p.getStock(),
                    p.getCategory());
        }
        System.out.println();
    }

    public void displayProductDetails(Product product) {
        displayProduct(product);
    }

    public Map<String, String> getProductInput() {
        Map<String, String> data = new HashMap<>();

        System.out.println("\nAdd New Product\n");
        String productId = Input.promptProductId("Product ID (format: P### e.g. P001, P002): ");
        data.put("id", productId.equalsIgnoreCase("exit") ? "exit" : productId.toUpperCase());
        data.put("name", Input.promptStringMaxLength("Product Name (max 50 characters): ", 50));
        data.put("price", String.valueOf(Input.promptPrice("Price (min Rp 1,000): Rp ")));
        data.put("stock", String.valueOf(Input.promptInt("Stock (min 0, e.g. 10): ")));
        data.put("description", Input.promptStringMaxLength("Description (max 200 characters): ", 200));
        data.put("category", Input.promptValidCategory("Select Product Category:"));

        return data;
    }

    public String getProductIdInput() {
        return Input.promptProductId("Enter Product ID (e.g. P001, P002): ");
    }

    public Map<String, String> getProductUpdateInput() {
        Map<String, String> data = new HashMap<>();

        System.out.println("\nUpdate Product");
        System.out.println("(Leave blank to skip)\n");
        String name = Input.promptString("New Name (max 50 characters, blank to skip): ");
        data.put("name", name.isEmpty() ? "" : (name.length() > 50 ? name.substring(0, 50) : name));

        String price = Input.promptString("New Price (min Rp 1,000, blank to skip): ");
        data.put("price", price);

        String desc = Input.promptString("New Description (max 200 characters, blank to skip): ");
        data.put("description", desc.isEmpty() ? "" : (desc.length() > 200 ? desc.substring(0, 200) : desc));

        String cat = Input.promptString("New Category (Apparel/Accessories/Electronics/Books, blank to skip): ");
        if (!cat.isEmpty() && !cat.equalsIgnoreCase("Apparel") && !cat.equalsIgnoreCase("Accessories") &&
                !cat.equalsIgnoreCase("Electronics") && !cat.equalsIgnoreCase("Books")) {
            System.out.println("Invalid category. Skipping.");
            data.put("category", "");
        } else {
            data.put("category", cat);
        }

        return data;
    }
}
