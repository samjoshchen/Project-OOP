package com.martminds.model.order;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import com.martminds.enums.*;
import com.martminds.model.common.*;
import com.martminds.exception.*;
import com.martminds.util.DateTimeUtil;

public class Order {
    private String orderId;
    private String customerId;
    private String storeId;
    private String driverId;
    
    private double totalPrice;
    private Address deliveryAddress;
    private List<OrderItem> items;

    private OrderStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime lastUpdatedAt;

    public Order(String orderId, String customerId, String storeId, Address deliveryAddress)
            throws InvalidOrderException {
        if (orderId == null || orderId.trim().isEmpty()) {
            throw new InvalidOrderException("Order ID cannot be empty");
        }
        if (customerId == null || customerId.trim().isEmpty()) {
            throw new InvalidOrderException("Customer ID cannot be empty");
        }
        if (storeId == null || storeId.trim().isEmpty()) {
            throw new InvalidOrderException("Store ID cannot be empty");
        }
        if (deliveryAddress == null) {
            throw new InvalidOrderException("Delivery address cannot be null");
        }
        if (!deliveryAddress.isComplete()) {
            throw new InvalidOrderException("Delivery address is incomplete");
        }

        this.orderId = orderId;
        this.customerId = customerId;
        this.storeId = storeId;
        this.deliveryAddress = deliveryAddress;

        this.items = new ArrayList<>();
        this.totalPrice = 0;
        this.driverId = null;

        this.status = OrderStatus.PENDING;

        this.createdAt = DateTimeUtil.now();
        this.lastUpdatedAt = DateTimeUtil.now();
    }

    public String getOrderId() {
        return orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getStoreId() {
        return storeId;
    }

    public String getDriverId() {
        return driverId;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public Address getDeliveryAddress() {
        return deliveryAddress;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public List<OrderItem> getItems() {
        return new ArrayList<>(items);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public void addItem(OrderItem item) throws InvalidOrderException {
        if (item == null) {
            throw new InvalidOrderException("Cannot add null item to order", orderId);
        }
        if (status != OrderStatus.PENDING) {
            throw new InvalidOrderException("Cannot modify order items after order status is " + status, orderId);
        }
        for (OrderItem existing : items) {
            if (existing.getProductId().equals(item.getProductId())) {
                throw new InvalidOrderException("Product already exists in order", orderId);
            }
        }
        items.add(item);
        calculateTotal();
        updateTimestamp();
    }

    public void removeItem(String orderItemId) throws InvalidOrderException {
        if (orderItemId == null || orderItemId.trim().isEmpty()) {
            throw new InvalidOrderException("Order item ID cannot be empty", orderId);
        }
        if (status != OrderStatus.PENDING) {
            throw new InvalidOrderException("Cannot modify order items after order status is " + status, orderId);
        }
        boolean removed = items.removeIf(item -> item.getOrderItemId().equals(orderItemId));
        if (!removed) {
            throw new InvalidOrderException("Order item not found: " + orderItemId, orderId);
        }
        if (items.isEmpty()) {
            throw new InvalidOrderException("Cannot remove last item. Cancel order instead.", orderId);
        }
        calculateTotal();
        updateTimestamp();
    }

    public void calculateTotal() {
        double sum = 0;
        for (OrderItem item : items) {
            sum += item.calculateSubtotal();
        }
        this.totalPrice = sum;
    }

    public void updateStatus(OrderStatus newStatus) throws InvalidOrderException {
        if (newStatus == null) {
            throw new InvalidOrderException("Order status cannot be null", orderId);
        }
        if (!isValidStatusTransition(this.status, newStatus)) {
            throw new InvalidOrderException(
                    String.format("Invalid status transition from %s to %s", this.status, newStatus), orderId);
        }
        this.status = newStatus;
        updateTimestamp();
    }

    public void assignDriver(String driverId) throws InvalidOrderException {
        if (driverId == null || driverId.trim().isEmpty()) {
            throw new InvalidOrderException("Driver ID cannot be empty", orderId);
        }
        if (this.status != OrderStatus.CONFIRMED && this.status != OrderStatus.READY_FOR_PICKUP) {
            throw new InvalidOrderException("Can only assign driver to confirmed or ready orders", orderId);
        }
        if (this.driverId != null && !this.driverId.equals(driverId)) {
            throw new InvalidOrderException("Order already assigned to driver: " + this.driverId, orderId);
        }
        this.driverId = driverId;
        this.status = OrderStatus.OUT_FOR_DELIVERY;
        updateTimestamp();
    }

    public void markAsDelivered() throws InvalidOrderException {
        if (this.status != OrderStatus.OUT_FOR_DELIVERY) {
            throw new InvalidOrderException("Can only mark orders as delivered when status is OUT_FOR_DELIVERY",
                    orderId);
        }
        if (this.driverId == null) {
            throw new InvalidOrderException("Cannot mark as delivered without assigned driver", orderId);
        }
        this.status = OrderStatus.DELIVERED;
        updateTimestamp();
    }

    public void cancel() throws InvalidOrderException {
        if (this.status == OrderStatus.DELIVERED) {
            throw new InvalidOrderException("Cannot cancel delivered orders", orderId);
        }
        if (this.status == OrderStatus.CANCELLED) {
            throw new InvalidOrderException("Order is already cancelled", orderId);
        }
        this.status = OrderStatus.CANCELLED;
        updateTimestamp();
    }

    private boolean isValidStatusTransition(OrderStatus from, OrderStatus to) {
        switch (from) {
            case PENDING:
                return to == OrderStatus.CONFIRMED || to == OrderStatus.CANCELLED;
            case CONFIRMED:
                return to == OrderStatus.PREPARING || to == OrderStatus.CANCELLED;
            case PREPARING:
                return to == OrderStatus.READY_FOR_PICKUP || to == OrderStatus.CANCELLED;
            case READY_FOR_PICKUP:
                return to == OrderStatus.OUT_FOR_DELIVERY || to == OrderStatus.CANCELLED;
            case OUT_FOR_DELIVERY:
                return to == OrderStatus.DELIVERED || to == OrderStatus.CANCELLED;
            case DELIVERED:
            case CANCELLED:
                return false;
            default:
                return false;
        }
    }
    // #endregion

    public String toFileString() {
        return String.format("%s,%s,%s,%s,%.2f,%s,%s,%s,%s",
                orderId, customerId, storeId,
                driverId != null ? driverId : "",
                totalPrice, status.name(),
                deliveryAddress.toString(),
                DateTimeUtil.formatForFile(createdAt),
                DateTimeUtil.formatForFile(lastUpdatedAt));
    }

    @Override
    public String toString() {
        return String.format("Order[ID=%s, Customer=%s, Store=%s, Status=%s, Total=%.2f, Items=%d, Created=%s]",
                orderId, customerId, storeId, status, totalPrice, items.size(),
                DateTimeUtil.formatForDisplay(createdAt));
    }

    private void updateTimestamp() {
        this.lastUpdatedAt = DateTimeUtil.now();
    }

    public boolean isRepurchasable() {
        return status == OrderStatus.DELIVERED;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public int getItemCount() {
        return items.stream().mapToInt(OrderItem::getQuantity).sum();
    }
}
