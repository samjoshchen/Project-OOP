package com.martminds.exception;

public class InvalidOrderException extends Exception {
    private String orderId;

    public InvalidOrderException(String message) {
        super(message);
    }

    public InvalidOrderException(String message, String orderId) {
        super(message);
        this.orderId = orderId;
    }

    public String getOrderId() {
        return orderId;
    }
}
