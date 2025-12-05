package com.martminds.enums;

public enum PaymentMethod {
    CASH("Cash", "Pay with cash on delivery"),
    CREDIT_CARD("Credit Card", "Pay with credit card"),
    EWALLET("E-Wallet", "Pay with digital wallet");

    private final String displayName;
    private final String description;

    PaymentMethod(String displayName, String description) {
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
