package com.martminds.model.order;

public class OrderItem 
{
    private String orderItemId;
    private String productId;
    private String productName;
    private int quantity;
    private final double priceAtPurchase;

    public OrderItem(String orderItemId, String productId, String productName, int quantity, double priceAtPurchase) 
    {
        this.orderItemId = orderItemId;
        this.productId = productId;
        this.productName = productName;
        setQuantity(quantity);
        this.priceAtPurchase = priceAtPurchase;
    }

    public double calculateSubtotal() 
    {
        return priceAtPurchase * quantity;
    }

    public void increaseQuantity(int amount) 
    {
        setQuantity(this.quantity + amount);
    }

    public void decreaseQuantity(int amount) 
    {
        setQuantity(this.quantity - amount);
    }

    private void setQuantity(int quantity) 
    {
        if(quantity < 1) 
        {
            throw new IllegalArgumentException("Quantity must be at least 1");
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
}
