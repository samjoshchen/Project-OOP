package com.martminds.model.order;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import com.martminds.model.common.*;;

public class Order
{
    private int orderId;
    private int customerId;
    private int storeId;
    private Integer driverId;
    
    private double totalPrice;
    private Address deliveryAddress;
    private List<OrderItem> items;
    
    private OrderStatus status;
    
    private LocalDateTime timestampCreatedAt;
    private LocalDateTime timestampUpdatedAt;

    public Order(int orderId, int customerId, int storeId, Address deliveryAddress)
    {
        this.orderId = orderId;
        this.customerId = customerId;
        this.storeId = storeId;
        this.deliveryAddress = deliveryAddress;
        
        this.items = new ArrayList<>();
        this.totalPrice = 0;
        
        this.status = OrderStatus.PENDING;
        
        this.timestampCreatedAt = LocalDateTime.now();
        this.timestampUpdatedAt = LocalDateTime.now();
    }

    public int getOrderId()
    {
        return orderId;
    }

    public void setOrderId(int orderId)
    {
        this.orderId = orderId;
        updateTimestamp();
    }

    public int getCustomerId()
    {
        return customerId;
    }

    public int getStoreId()
    {
        return storeId;
    }

    public Integer getDriverId()
    {
        return driverId;
    }

    public void setDriverId(Integer driverId)
    {
        this.driverId = driverId;
        updateTimestamp();
    }

    public double getTotalPrice()
    {
        return totalPrice;
    }

    public OrderStatus getStatus()
    {
        return status;
    }

    public void setStatus(OrderStatus status)
    {
        this.status = status;
        updateTimestamp();
    }

    public List<OrderItem> getItems()
    {
        return items;
    }

    public LocalDateTime getTimestampCreatedAt()
    {
        return timestampCreatedAt;
    }

    public LocalDateTime getTimestampUpdatedAt()
    {
        return timestampUpdatedAt;
    }

    public void addItem(OrderItem item)
    {
        items.add(item);
        calculateTotal();
        updateTimestamp();
    }

    public void removeItem(String orderItemId)
    {
        items.removeIf(item -> item.getOrderItemId() == orderItemId);
        calculateTotal();
        updateTimestamp();
    }

    public void calculateTotal()
    {
        double sum = 0;
        for (OrderItem item : items)
        {
            sum += item.calculateSubtotal();
        }
        totalPrice = sum;
    }

    public void assignDriver(int driverId)
    {
        this.driverId = driverId;
        this.status = OrderStatus.SHIPPED;
        updateTimestamp();
    }

    public boolean isRepurchasable()
    {
        return status == OrderStatus.DELIVERED;
    }

    private void updateTimestamp()
    {
        this.timestampUpdatedAt = LocalDateTime.now();
    }
}

