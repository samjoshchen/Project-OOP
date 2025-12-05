package com.martminds.enums;

public enum PaymentStatus {
    PENDING("Pending", "Payment is being processed"),
    SUCCESS("Success", "Payment completed successfully"),
    FAILED("Failed", "Payment failed"),
    REFUNDED("Refunded", "Payment has been refunded"),
    CANCELLED("Cancelled", "Payment was cancelled");

    private final String displayName;
    private final String description;

    PaymentStatus(String displayName, String description) {
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
