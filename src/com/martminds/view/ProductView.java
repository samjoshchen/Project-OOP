package com.martminds.view;

import com.martminds.model.product.Product;
import java.util.List;

public class ProductView 
{

    public void displayProduct(Product product) 
    {
        System.out.println("=== Product Details ===");
        System.out.println("ID: " + product.getProductId());
        System.out.println("Name: " + product.getName());
        System.out.println("Category: " + product.getCategory());
        System.out.println("Price: $" + product.getPrice());
        System.out.println("Stock: " + product.getStock());
        System.out.println("Description: " + product.getDescription());
        System.out.println("Store ID: " + product.getStoreId());
        System.out.println("Available: " + (product.isAvailable() ? "Yes" : "No"));
        System.out.println("=======================");
    }

    public void displayProductList(List<Product> products) 
    {
        System.out.println("=== Product List ===");
        for (Product product : products) 
        {
            System.out.println(product.getProductId() + " - " + product.getName() +
                               " ($" + product.getPrice() + ") | Stock: " + product.getStock());
        }
        System.out.println("====================");
    }
}
