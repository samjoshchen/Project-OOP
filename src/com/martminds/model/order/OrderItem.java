package com.martminds.model.order;

import com.martminds.exception.InvalidOrderException;

public class OrderItem 
{
    private String orderItemId;
    private String productId;
    private String productName;
    private int quantity;
    private final double priceAtPurchase;

    public OrderItem(String orderItemId, String productId, String productName, int quantity, double priceAtPurchase) throws InvalidOrderException
    {
        if (orderItemId == null || orderItemId.trim().isEmpty()) {
            throw new InvalidOrderException("Order item ID cannot be empty");
        }
        if (productId == null || productId.trim().isEmpty()) {
            throw new InvalidOrderException("Product ID cannot be empty");
        }
        if (productName == null || productName.trim().isEmpty()) {
            throw new InvalidOrderException("Product name cannot be empty");
        }
        if (priceAtPurchase <= 0) {
            throw new InvalidOrderException("Price must be greater than 0");
        }

        this.orderItemId = orderItemId;
        this.productId = productId;
        this.productName = productName;
        setQuantity(quantity);
        this.priceAtPurchase = priceAtPurchase;
    }

    public void setQuantity(int quantity) throws InvalidOrderException
    {
        if(quantity < 1) 
        {
            throw new InvalidOrderException("Quantity must be at least 1");
        }
        this.quantity = quantity;
    }

    public String getOrderItemId() 
    {
        return orderItemId;
    }

    public String getProductId() 
    {
        return productId;
    }

    public String getProductName() 
    {
        return productName;
    }

    public int getQuantity() 
    {
        return quantity;
    }

    public double getPriceAtPurchase() 
    {
        return priceAtPurchase;
    }

    public double calculateSubtotal() 
    {
        return priceAtPurchase * quantity;
    }

    public void increaseQuantity(int amount) throws InvalidOrderException
    {
        if (amount <= 0) {
            throw new InvalidOrderException("Amount to increase must be positive");
        }
        setQuantity(this.quantity + amount);
    }

    public void decreaseQuantity(int amount) throws InvalidOrderException
    {
        if (amount <= 0) {
            throw new InvalidOrderException("Amount to decrease must be positive");
        }
        setQuantity(this.quantity - amount);
    }
}
