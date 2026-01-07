package com.martminds.exception;

public class OutOfStockException extends Exception {
    private String productId;
    private int requestedQuantity;
    private int availableStock;

    public OutOfStockException(String message) {
        super(message);
    }

    public OutOfStockException(String message, String productId, int requestedQuantity, int availableStock) {
        super(message);
        this.productId = productId;
        this.requestedQuantity = requestedQuantity;
        this.availableStock = availableStock;
    }

    public String getProductId() {
        return productId;
    }

    public int getRequestedQuantity() {
        return requestedQuantity;
    }

    public int getAvailableStock() {
        return availableStock;
    }
}
