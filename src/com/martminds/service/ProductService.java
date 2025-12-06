package com.martminds.service;

import java.util.ArrayList;
import java.util.List;
import com.martminds.model.product.Product;

public class ProductService 
{
    private static ArrayList<Product> products = new ArrayList<>();

    public ProductService() 
    {
        products.add(new Product("P001", "Laptop", 1000.0, 5, "Gaming Laptop", "S001", "Electronics"));
        products.add(new Product("P002", "Headphones", 50.0, 10, "Wireless Headphones", "S001", "Electronics"));
        products.add(new Product("P003", "Mouse", 25.0, 0, "Wireless Mouse", "S002", "Electronics")); // out of stock example
    }

    public Product findProductById(String id) 
    {
        for (Product p : products) 
        {
            if (p.getProductId().equals(id)) 
            {
                return p;
            }
        }
        return null;
    }

    public List<Product> getAllProducts() 
    {
        return products;
    }

    public void updateProductStock(String productId, int quantity) 
    {
        Product p = findProductById(productId);
        if (p != null) 
        {
            p.updateStock(quantity);
        }
    }
}
