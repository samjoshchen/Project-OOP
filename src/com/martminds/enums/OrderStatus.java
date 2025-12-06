package com.martminds.enums;

public enum OrderStatus {
    PENDING("Pending", "Order has been placed and awaiting confirmation"),
    CONFIRMED("Confirmed", "Order has been confirmed by the store"),
    PREPARING("Preparing", "Store is preparing the order"),
    READY_FOR_PICKUP("Ready for Pickup", "Order is ready to be picked up by driver"),
    OUT_FOR_DELIVERY("Out for Delivery", "Driver is delivering the order"),
    DELIVERED("Delivered", "Order has been successfully delivered"),
    CANCELLED("Cancelled", "Order has been cancelled");

    private final String displayName;
    private final String description;

    OrderStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
