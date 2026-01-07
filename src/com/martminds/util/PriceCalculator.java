package com.martminds.util;

public class PriceCalculator {
    private static final double TAX_RATE = 0.11;
    private static final double DELIVERY_FEE_PER_KM = 2000;
    private static final double BASE_DELIVERY_FEE = 5000;

    public static double calculateTax(double subtotal) {
        return subtotal * TAX_RATE;
    }

    public static double calculateDeliveryFee(double distanceKm) {
        return BASE_DELIVERY_FEE + (distanceKm * DELIVERY_FEE_PER_KM);
    }

    public static double calculateTotal(double subtotal, double distanceKm) {
        double tax = calculateTax(subtotal);
        double deliveryFee = calculateDeliveryFee(distanceKm);
        return subtotal + tax + deliveryFee;
    }

    public static double calculateDiscount(double amount, double discountPercent) {
        if (discountPercent < 0 || discountPercent > 100) {
            return 0;
        }
        return amount * (discountPercent / 100);
    }

    public static double applyDiscount(double amount, double discountPercent) {
        double discount = calculateDiscount(amount, discountPercent);
        return amount - discount;
    }

    public static String formatPrice(double price) {
        return String.format("Rp %.0f", price);
    }
}
