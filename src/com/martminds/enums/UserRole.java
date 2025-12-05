package com.martminds.enums;

public enum UserRole {
    CUSTOMER("Customer", "Regular customer who places orders"),
    DRIVER("Driver", "Delivery driver who fulfills orders"),
    ADMIN("Admin", "System administrator with full access");

    private final String displayName;
    private final String description;

    UserRole(String displayName, String description) {
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
