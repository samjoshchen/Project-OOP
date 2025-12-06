package com.martminds.model.order;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.martminds.enums.OrderStatus;
import com.martminds.model.common.Address;
import com.martminds.model.product.Product;
import com.martminds.exception.InvalidOrderException;
import com.martminds.util.DateTimeUtil;

public class MysteryBoxOrder {
    private String boxOrderId;
    private String customerId;
    private String boxId;
    private String boxName;

    private OrderStatus status;
    private double price;
    private Address deliveryAddress;
    private String driverId;

    private List<Product> actualContents;
    private boolean contentsRevealed;

    private LocalDateTime createdAt;
    private LocalDateTime lastUpdatedAt;

    public MysteryBoxOrder(String boxOrderId, String customerId, String boxId, String boxName, double price,
            Address deliveryAddress) throws InvalidOrderException {
        if (boxOrderId == null || boxOrderId.trim().isEmpty()) {
            throw new InvalidOrderException("Mystery box order ID cannot be empty");
        }
        if (customerId == null || customerId.trim().isEmpty()) {
            throw new InvalidOrderException("Customer ID cannot be empty");
        }
        if (boxId == null || boxId.trim().isEmpty()) {
            throw new InvalidOrderException("Box ID cannot be empty");
        }
        if (boxName == null || boxName.trim().isEmpty()) {
            throw new InvalidOrderException("Box name cannot be empty");
        }
        if (price <= 0) {
            throw new InvalidOrderException("Price must be greater than 0");
        }
        if (deliveryAddress == null) {
            throw new InvalidOrderException("Delivery address cannot be null");
        }
        if (!deliveryAddress.isComplete()) {
            throw new InvalidOrderException("Delivery address is incomplete");
        }

        this.boxOrderId = boxOrderId;
        this.customerId = customerId;
        this.boxId = boxId;
        this.boxName = boxName;
        this.price = price;
        this.deliveryAddress = deliveryAddress;

        this.status = OrderStatus.PENDING;
        this.driverId = null;
        this.actualContents = new ArrayList<>();
        this.contentsRevealed = false;

        this.createdAt = DateTimeUtil.now();
        this.lastUpdatedAt = DateTimeUtil.now();
    }

    public String getBoxOrderId() {
        return boxOrderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getBoxId() {
        return boxId;
    }

    public String getBoxName() {
        return boxName;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public double getPrice() {
        return price;
    }

    public Address getDeliveryAddress() {
        return deliveryAddress;
    }

    public String getDriverId() {
        return driverId;
    }

    public boolean isContentsRevealed() {
        return contentsRevealed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public void setActualContents(List<Product> products) throws InvalidOrderException {
        if (products == null || products.isEmpty()) {
            throw new InvalidOrderException("Mystery box contents cannot be empty", boxOrderId);
        }
        if (!actualContents.isEmpty()) {
            throw new InvalidOrderException("Mystery box contents already set", boxOrderId);
        }
        if (status != OrderStatus.PENDING && status != OrderStatus.CONFIRMED) {
            throw new InvalidOrderException("Can only set contents for pending or confirmed orders", boxOrderId);
        }
        this.actualContents = new ArrayList<>(products);
        updateTimestamp();
    }

    public List<Product> revealContents() throws InvalidOrderException {
        if (actualContents.isEmpty()) {
            throw new InvalidOrderException("Mystery box contents not yet determined", boxOrderId);
        }
        if (status != OrderStatus.DELIVERED) {
            throw new InvalidOrderException("Contents can only be revealed after delivery", boxOrderId);
        }
        this.contentsRevealed = true;
        updateTimestamp();
        return new ArrayList<>(actualContents);
    }

    public void updateStatus(OrderStatus newStatus) throws InvalidOrderException {
        if (newStatus == null) {
            throw new InvalidOrderException("Order status cannot be null", boxOrderId);
        }
        if (!isValidStatusTransition(this.status, newStatus)) {
            throw new InvalidOrderException(
                    String.format("Invalid status transition from %s to %s", this.status, newStatus), boxOrderId);
        }
        this.status = newStatus;
        updateTimestamp();
    }

    public void assignDriver(String driverId) throws InvalidOrderException {
        if (driverId == null || driverId.trim().isEmpty()) {
            throw new InvalidOrderException("Driver ID cannot be empty", boxOrderId);
        }
        if (this.status != OrderStatus.CONFIRMED && this.status != OrderStatus.READY_FOR_PICKUP) {
            throw new InvalidOrderException("Can only assign driver to confirmed or ready orders", boxOrderId);
        }
        if (this.driverId != null && !this.driverId.equals(driverId)) {
            throw new InvalidOrderException("Order already assigned to driver: " + this.driverId, boxOrderId);
        }
        this.driverId = driverId;
        this.status = OrderStatus.OUT_FOR_DELIVERY;
        updateTimestamp();
    }

    public void markAsDelivered() throws InvalidOrderException {
        if (this.status != OrderStatus.OUT_FOR_DELIVERY) {
            throw new InvalidOrderException("Can only mark orders as delivered when status is OUT_FOR_DELIVERY",
                    boxOrderId);
        }
        if (this.driverId == null) {
            throw new InvalidOrderException("Cannot mark as delivered without assigned driver", boxOrderId);
        }
        if (actualContents.isEmpty()) {
            throw new InvalidOrderException("Cannot deliver mystery box without contents", boxOrderId);
        }
        this.status = OrderStatus.DELIVERED;
        updateTimestamp();
    }

    public void cancel() throws InvalidOrderException {
        if (this.status == OrderStatus.DELIVERED) {
            throw new InvalidOrderException("Cannot cancel delivered orders", boxOrderId);
        }
        if (this.status == OrderStatus.CANCELLED) {
            throw new InvalidOrderException("Order is already cancelled", boxOrderId);
        }
        this.status = OrderStatus.CANCELLED;
        updateTimestamp();
    }

    public int getActualContentsCount() {
        return actualContents.size();
    }

    public List<Product> getActualContents() {
        if (!contentsRevealed) {
            return new ArrayList<>();
        }
        return new ArrayList<>(actualContents);
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

    private void updateTimestamp() {
        this.lastUpdatedAt = DateTimeUtil.now();
    }

    public String toFileString() {
        return String.format("%s,%s,%s,%s,%.2f,%s,%s,%s,%s,%s,%b",
                boxOrderId, customerId, boxId, boxName, price,
                status.name(), deliveryAddress.toString(),
                driverId != null ? driverId : "",
                DateTimeUtil.formatForFile(createdAt),
                DateTimeUtil.formatForFile(lastUpdatedAt),
                contentsRevealed);
    }

    @Override
    public String toString() {
        return String.format(
                "MysteryBoxOrder[ID=%s, Customer=%s, Box=%s, Status=%s, Price=%.2f, Revealed=%s, Created=%s]",
                boxOrderId, customerId, boxName, status, price,
                contentsRevealed ? "Yes" : "No",
                DateTimeUtil.formatForDisplay(createdAt));
    }
}
