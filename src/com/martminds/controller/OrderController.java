package com.martminds.controller;

import com.martminds.model.order.Order;
import com.martminds.model.order.OrderItem;
import com.martminds.model.common.Address;
import com.martminds.model.user.User;
import com.martminds.service.OrderService;
import com.martminds.service.ProductService;
import com.martminds.util.Session;
import com.martminds.exception.InvalidOrderException;
import com.martminds.exception.OutOfStockException;

import java.util.List;
import java.util.UUID;

public class OrderController 
{
    private final OrderService orderService;
    private final ProductService productService;

    public OrderController(OrderService orderService, ProductService productService) 
    {
        this.orderService = orderService;
        this.productService = productService;
    }

    public Order createOrder(String storeId, List<OrderItem> items, Address address) throws InvalidOrderException, OutOfStockException 
    {
        Session session = Session.getInstance();
        session.requireCustomer(); 

        User currentUser = session.getCurrentUser();

        String orderId = UUID.randomUUID().toString();
        Order order = new Order(orderId, currentUser.getUserId(), storeId, address);

        for (OrderItem item : items) 
        {
            order.addItem(item);
        }

        return orderService.createOrder(order);
    }

    public List<Order> getMyOrders() throws InvalidOrderException 
    {
        Session session = Session.getInstance();
        session.requireCustomer();

        User currentUser = session.getCurrentUser();
        return orderService.getOrdersByCustomer(currentUser.getUserId());
    }

    public Order getOrderDetails(String orderId) 
    {
        return orderService.findOrderById(orderId);
    }
}
