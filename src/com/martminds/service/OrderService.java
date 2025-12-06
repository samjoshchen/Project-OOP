package com.martminds.service;

import java.util.ArrayList;
import java.util.List;
import com.martminds.model.order.Order;
import com.martminds.model.order.OrderItem;
import com.martminds.enums.OrderStatus;
import com.martminds.model.product.Product;
import com.martminds.exception.InvalidOrderException;
import com.martminds.exception.OutOfStockException;

public class OrderService 
{
    private static ArrayList<Order> orders = new ArrayList<>();
    private ProductService productService;

    public OrderService(ProductService productService) 
    {
        this.productService = productService;
    }

    public Order createOrder(Order order) throws OutOfStockException 
    {
        for (OrderItem item : order.getItems()) 
        {
            Product product = productService.findProductById(item.getProductId());
            if (product == null || product.getStock() < item.getQuantity()) 
            {
                throw new OutOfStockException("Product " + (product != null ? product.getName() : item.getProductId()) + " is out of stock!");
            }
        }

        for (OrderItem item : order.getItems()) 
        {
            Product product = productService.findProductById(item.getProductId());
            product.updateStock(-item.getQuantity());
        }

        orders.add(order);
        return order;
    }

    public Order findOrderById(String id) 
    {
        for (Order o : orders) 
        {
            if (o.getOrderId().equals(id)) 
            {
                return o;
            }
        }
        return null;
    }

    public List<Order> getOrdersByCustomer(String customerId) 
    {
        List<Order> customerOrders = new ArrayList<>();
        for (Order o : orders) 
        {
            if (o.getCustomerId().equals(customerId)) 
            {
                customerOrders.add(o);
            }
        }
        return customerOrders;
    }

    public void updateOrderStatus(String orderId, OrderStatus newStatus) throws InvalidOrderException
    {
        Order order = findOrderById(orderId);
        if (order != null)
        {
            order.updateStatus(newStatus); 
        }
    }
}
